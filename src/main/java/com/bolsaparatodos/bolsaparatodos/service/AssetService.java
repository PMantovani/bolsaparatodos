package com.bolsaparatodos.bolsaparatodos.service;

import com.bolsaparatodos.bolsaparatodos.entity.financial.Asset;
import com.bolsaparatodos.bolsaparatodos.repository.AssetRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
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

        if (tokenResponse == null) {
            return;
        }

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

            if (assetApiResponse == null) {
                return;
            }

            String assetApiResponseString = new String(assetApiResponse, StandardCharsets.ISO_8859_1);
            String[] assetLines = assetApiResponseString.split("\\r?\\n");
            List<Asset> foundAssets = new ArrayList<>();
            for (int i=1; i<assetLines.length; i++) {
                String[] assetFields = assetLines[i].split(";");
                if (assetFields[5].equals("EQUITY-CASH")) {
                    if (assetFields[6].equals("SHARES")) {
                        foundAssets.add(new Asset(assetFields[1], Asset.AssetType.SHARE, assetFields[47]));
                    } else if (assetFields[6].equals("FUNDS")) {
                        foundAssets.add(new Asset(assetFields[1], Asset.AssetType.REAL_STATE_FUND, assetFields[47]));
                    } else if (assetFields[6].equals("BDR")) {
                        foundAssets.add(new Asset(assetFields[1], Asset.AssetType.BDR, assetFields[47]));
                    } else if (assetFields[6].equals("INDEX")) {
                        foundAssets.add(new Asset(assetFields[1], Asset.AssetType.INDEX, assetFields[47]));
                    } else if (assetFields[6].contains("ETF")) {
                        foundAssets.add(new Asset(assetFields[1], Asset.AssetType.ETF, assetFields[47]));
                    }
                }
            }

            assetRepository.saveAll(foundAssets);

        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
    }
}
