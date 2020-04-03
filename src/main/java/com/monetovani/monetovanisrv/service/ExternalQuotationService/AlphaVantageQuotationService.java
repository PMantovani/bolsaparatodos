package com.monetovani.monetovanisrv.service.ExternalQuotationService;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.monetovani.monetovanisrv.entity.financial.Asset;
import com.monetovani.monetovanisrv.entity.financial.MarketQuotation;
import com.monetovani.monetovanisrv.entity.financial.keys.MarketDataKeys;
import com.monetovani.monetovanisrv.repository.AssetRepository;
import com.monetovani.monetovanisrv.repository.MarketQuotationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.ExchangeStrategies;
import org.springframework.web.reactive.function.client.WebClient;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static java.time.LocalDate.parse;

@Service
public class AlphaVantageQuotationService implements QuotationService {

    @Autowired
    private MarketQuotationRepository marketQuotationRepository;

    @Autowired
    private AssetRepository assetRepository;

    @Autowired
    private Environment env;

    @Autowired
    private ObjectMapper objectMapper;

    public List<MarketQuotation> getQuotation(String assetCode, LocalDateTime startDate, LocalDateTime endDate) {
        final String functionQueryParamName = "function";
        final String functionQueryParamValue = "TIME_SERIES_DAILY";
        final String symbolQueryParamName = "symbol";
        final String apiKeyQueryParamName = "apikey";
        final String symbolSuffix = ".SA";
        final String outputSizeQueryParamName = "outputsize";
        final String outputSize = "full";

        List<MarketQuotation> result = new ArrayList<>();
        String baseUrl = env.getProperty("market-data-alphavantage-api-url");
        String apiKey = env.getProperty("market-data-alphavantage-api-key");

        Asset asset = assetRepository.findById(assetCode).get();
        String querySymbol = asset.getCode() + symbolSuffix;

        // Increase the memory in size so that we can retrieve the full history of the asset
        ExchangeStrategies exchangeStrategies = ExchangeStrategies.builder()
                .codecs(configurer -> configurer.defaultCodecs().maxInMemorySize(1024 * 1000)).build();

        String response = WebClient.builder()
                .baseUrl(baseUrl)
                .exchangeStrategies(exchangeStrategies)
                .build().get().uri(uriBuilder -> uriBuilder
                        .queryParam(functionQueryParamName, functionQueryParamValue)
                        .queryParam(symbolQueryParamName, querySymbol)
                        .queryParam(apiKeyQueryParamName, apiKey)
                        .queryParam(outputSizeQueryParamName, outputSize)
                        .build()).retrieve().bodyToMono(String.class).block();

        try {
            JsonNode json = objectMapper.readTree(response);
            ObjectNode quotations = (ObjectNode) json.path("Time Series (Daily)");
            quotations.fields().forEachRemaining(entry -> {
                MarketQuotation quotation = this.parseQuotationEntry(asset, entry);
                if (quotation.getId().getEventDate().isAfter(startDate.toLocalDate()) &&
                        quotation.getId().getEventDate().isBefore(endDate.toLocalDate())) {
                    result.add(quotation);
                }
            });
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }

        marketQuotationRepository.saveAll(result);
        return result;
    }

    private MarketQuotation parseQuotationEntry(Asset asset, Map.Entry<String, JsonNode> entry) {
        // Add quotation from external API to internal database
        MarketQuotation mq = new MarketQuotation();
        mq.setId(new MarketDataKeys(asset, parse(entry.getKey(), DateTimeFormatter.ISO_DATE)));
        mq.setCloseValue(Float.parseFloat(entry.getValue().path("4. close").asText()));
        return mq;
    }
}
