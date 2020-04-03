package com.monetovani.monetovanisrv.service.externalMarketDataService;

import com.monetovani.monetovanisrv.entity.financial.Asset;
import com.monetovani.monetovanisrv.entity.financial.MarketDividend;
import com.monetovani.monetovanisrv.entity.financial.MarketQuotation;
import com.monetovani.monetovanisrv.entity.financial.MarketSplit;
import com.monetovani.monetovanisrv.entity.financial.keys.MarketDataKeys;
import com.monetovani.monetovanisrv.model.MarketDataCreationResponse;
import com.monetovani.monetovanisrv.repository.AssetRepository;
import com.monetovani.monetovanisrv.repository.MarketDividendRepository;
import com.monetovani.monetovanisrv.repository.MarketQuotationRepository;
import com.monetovani.monetovanisrv.repository.MarketSplitRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.ExchangeStrategies;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Service
public class YahooFinanceQuotationService implements QuotationService, ExternalDividendsService, ExternalSplitService {

    @Autowired
    private MarketQuotationRepository marketQuotationRepository;

    @Autowired
    private MarketSplitRepository marketSplitRepository;

    @Autowired
    private MarketDividendRepository marketDividendRepository;

    @Autowired
    private AssetRepository assetRepository;

    @Autowired
    private Environment env;

    public List<MarketQuotation> getQuotation(String assetCode, LocalDateTime startDate, LocalDateTime endDate) {
        List<MarketQuotation> result = new ArrayList<>();

        final String symbolSuffix = ".SA";
        final String startDateQueryParamName = "period1";
        final String endDateQueryParamName = "period2";
        final String intervalQueryParamName = "interval";
        final String intervalQueryParamValue = "1d";
        final String eventsQueryParamName = "events";
        final String eventsQueryParamValue = "history";

        String baseUrl = env.getProperty("market-data-api-yahoofinance-url");
        Asset asset = assetRepository.findById(assetCode).get();
        String querySymbol = asset.getCode() + symbolSuffix;

        ExchangeStrategies exchangeStrategies = ExchangeStrategies.builder()
                .codecs(configurer -> configurer.defaultCodecs().maxInMemorySize(1024 * 1000)).build();

        String response = WebClient.builder()
                .baseUrl(baseUrl)
                .exchangeStrategies(exchangeStrategies)
                .build().get().uri(uriBuilder -> uriBuilder
                        .pathSegment(querySymbol)
                        .queryParam(startDateQueryParamName, startDate.toEpochSecond(ZoneOffset.MIN))
                        .queryParam(endDateQueryParamName, endDate.toEpochSecond(ZoneOffset.MAX))
                        .queryParam(intervalQueryParamName, intervalQueryParamValue)
                        .queryParam(eventsQueryParamName, eventsQueryParamValue)
                        .build()).retrieve().bodyToMono(String.class).block();

        String[] lines = response.split("\\r?\\n");
        String[] firstLineFields = lines[0].split(",");
        // Find "Adj Close" field position
        int closeFieldPosition = -1;
        int dateFieldPosition = -1;
        for (int i=0; i<firstLineFields.length; i++) {
            if (firstLineFields[i].contains("Adj Close")) {
                closeFieldPosition = i;
            } else if (firstLineFields[i].contains("Date")) {
                dateFieldPosition = i;
            }
        }

        // Skip header and iterate
        for (int i=1; i<lines.length; i++) {
            String[] fields = lines[i].split(",");

            if (!fields[dateFieldPosition].equals("null") && !fields[closeFieldPosition].equals("null")) {
                LocalDate quotationDate = LocalDate.parse(fields[dateFieldPosition], DateTimeFormatter.ISO_DATE);
                float closeValue = Float.parseFloat(fields[closeFieldPosition]);

                MarketQuotation mq = new MarketQuotation(new MarketDataKeys(asset, quotationDate), 0, 0, 0, closeValue, 0);
                result.add(mq);
            }
        }

        marketQuotationRepository.saveAll(result);

        return result;
    }

    public MarketDataCreationResponse processDividendsRequest(
            MarketDataCreationResponse results, LocalDate startDate, LocalDate endDate, boolean purgeExistent) {
        makeRequest(startDate, endDate, YahooQueryEventTypes.DIV);
        return results;
    }

    public MarketDataCreationResponse processSplitRequest(
            MarketDataCreationResponse results, LocalDate startDate, LocalDate endDate, boolean purgeExistent) {
        makeRequest(startDate, endDate, YahooQueryEventTypes.SPLIT);
        return results;
    }

    private void makeRequest(LocalDate startDate, LocalDate endDate, YahooQueryEventTypes eventType) {
        final String symbolSuffix = ".SA";
        final String startDateQueryParamName = "period1";
        final String endDateQueryParamName = "period2";
        final String intervalQueryParamName = "interval";
        final String intervalQueryParamValue = "1d";
        final String eventsQueryParamName = "events";

        String baseUrl = env.getProperty("market-data-api-yahoofinance-url");
        List<Asset> assets = assetRepository.findAll();

        ExchangeStrategies exchangeStrategies = ExchangeStrategies.builder()
                .codecs(configurer -> configurer.defaultCodecs().maxInMemorySize(1024 * 1000)).build();

        int totalCount = 0;
        for (Asset asset: assets) {
            if (!this.shouldMakeRequest(asset, eventType)) {
                continue;
            }

            String response = WebClient.builder()
                .baseUrl(baseUrl)
                .exchangeStrategies(exchangeStrategies)
                .build().get().uri(uriBuilder -> uriBuilder
                        .pathSegment(asset.getCode() + symbolSuffix)
                        .queryParam(startDateQueryParamName, startDate.atTime(0, 0).toEpochSecond(ZoneOffset.MIN))
                        .queryParam(endDateQueryParamName, endDate.atTime(23, 59).toEpochSecond(ZoneOffset.MAX))
                        .queryParam(intervalQueryParamName, intervalQueryParamValue)
                        .queryParam(eventsQueryParamName, eventType)
                        .build())
                .retrieve().bodyToMono(String.class)
                .onErrorReturn(WebClientResponseException.NotFound.class, "")
                .block();

            String[] lines = response.split("\\r?\\n");
            switch (eventType) {
                case SPLIT:
                    this.formatSplitData(asset, lines);
                    break;
                case DIV:
                    this.formatDividendsData(asset, lines);
                    break;
            }

            totalCount += lines.length - 1;

            // Wait to make another request so that we don't exceed API's request limit
            try { Thread.sleep(100); } catch (InterruptedException e) {}
        }
    }

    private boolean shouldMakeRequest(Asset asset, YahooQueryEventTypes eventType) {
        return !asset.isOddLot() || (eventType.equals(YahooQueryEventTypes.HISTORY));
    }

    private void formatSplitData(Asset asset, String[] lines) {
        List<MarketSplit> result = new ArrayList<>();
        // Skip header and iterate
        for (int i=1; i<lines.length; i++) {
            String[] fields = lines[i].split(",");

            LocalDate eventDate = LocalDate.parse(fields[0], DateTimeFormatter.ISO_DATE);
            float beforeSplit = Float.parseFloat(fields[1].split(":")[0]);
            float afterSplit = Float.parseFloat(fields[1].split(":")[1]);
            float splitFactor = 1;
            if (beforeSplit != 0) {
                splitFactor = afterSplit / beforeSplit;
            }
            String splitRatio = fields[1];

            MarketSplit ms = new MarketSplit(new MarketDataKeys(asset, eventDate), splitFactor, splitRatio);
            result.add(ms);
        }

        marketSplitRepository.saveAll(result);

        // Add odd lot information as well
        if (!asset.isOddLot()) {
            assetRepository.findById(asset.getCode() + "F").ifPresent(oddLotAsset -> {
                List<MarketSplit> oddLotInfo = new ArrayList<>();
                for (MarketSplit msEventLot: result) {
                    MarketDataKeys msOddLotKeys = new MarketDataKeys(oddLotAsset, msEventLot.getId().getEventDate());
                    oddLotInfo.add(new MarketSplit(msOddLotKeys, msEventLot.getSplitFactor(), msEventLot.getSplitRatio()));
                }

                marketSplitRepository.saveAll(oddLotInfo);
            });
        }
    }

    private void formatDividendsData(Asset asset, String[] lines) {
        List<MarketDividend> result = new ArrayList<>();
        // Skip header and iterate
        for (int i=1; i<lines.length; i++) {
            String[] fields = lines[i].split(",");

            LocalDate eventDate = LocalDate.parse(fields[0], DateTimeFormatter.ISO_DATE);
            float dividendsPerShare = Float.parseFloat(fields[1]);

            MarketDividend md = new MarketDividend(new MarketDataKeys(asset, eventDate), dividendsPerShare);
            result.add(md);
        }

        marketDividendRepository.saveAll(result);

        // Add odd lot information as well
        if (!asset.isOddLot()) {
            assetRepository.findById(asset.getCode() + "F").ifPresent(oddLotAsset -> {
                List<MarketDividend> oddLotInfo = new ArrayList<>();
                for (MarketDividend mdEventLot: result) {
                    MarketDataKeys mdOddLotKeys = new MarketDataKeys(oddLotAsset, mdEventLot.getId().getEventDate());
                    oddLotInfo.add(new MarketDividend(mdOddLotKeys, mdEventLot.getDividendPerShare()));
                }

                marketDividendRepository.saveAll(oddLotInfo);
            });
        }
    }

    private enum YahooQueryEventTypes {
        HISTORY, DIV, SPLIT;

        @Override
        public String toString() {
            return name().toLowerCase();
        }
    }
}
