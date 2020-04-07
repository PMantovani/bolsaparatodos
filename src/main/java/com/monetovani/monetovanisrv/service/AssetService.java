package com.monetovani.monetovanisrv.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.monetovani.monetovanisrv.entity.financial.Asset;
import com.monetovani.monetovanisrv.repository.AssetRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.ExchangeStrategies;
import org.springframework.web.reactive.function.client.WebClient;

import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
public class AssetService {

    @Autowired
    private Environment env;


    @Autowired
    private AssetRepository assetRepository;

    public List<Asset> getAllAssets() {
        return assetRepository.findAll();
    }

    public void syncAssets(LocalDate date, boolean purgeExistent) {
        if (purgeExistent) {
            this.assetRepository.deleteAll();
        }
        this.syncAssetsFromB3(date);
    }

    private void syncAssetsFromB3(LocalDate date) {

        String tokenResponse = WebClient.builder()
                .baseUrl(env.getProperty("asset-api-token-url"))
                .build().get().uri(uriBuilder -> uriBuilder
                        .queryParam("fileName", "InstrumentsConsolidated")
                        .queryParam("date", date.toString())
                        .build()).retrieve().bodyToMono(String.class).block();

        try {
            JsonNode json = new ObjectMapper().readTree(tokenResponse);
            String token = json.path("token").asText();

            ExchangeStrategies exchangeStrategies = ExchangeStrategies.builder()
                    .codecs(configurer -> configurer.defaultCodecs().maxInMemorySize(1024 * 1024 * 50)).build();

            byte[] assetApiResponse = WebClient.builder()
                    .baseUrl(env.getProperty("asset-api-url"))
                    .exchangeStrategies(exchangeStrategies)
                    .build()
                    .get().uri(uriBuilder -> uriBuilder
                            .queryParam("token", token)
                            .build())
                    .acceptCharset(StandardCharsets.UTF_8)
                    .retrieve().bodyToMono(byte[].class).block();

            String assetApiResponseString = new String(assetApiResponse, StandardCharsets.ISO_8859_1);
            String[] assetLines = assetApiResponseString.split("\\r?\\n");
            List<Asset> foundAssets = new ArrayList<>();
            for (int i=1; i<assetLines.length; i++) {
                String[] assetFields = assetLines[i].split(";");
                if (assetFields[5].equals("EQUITY-CASH")) {
                    foundAssets.add(new Asset(assetFields[1], Asset.AssetType.SHARE, assetFields[47]));
                }
            }

            assetRepository.saveAll(foundAssets);

        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
    }
}
