package com.monetovani.monetovanisrv.service.externalMarketDataService;

import com.monetovani.monetovanisrv.entity.financial.Asset;
import com.monetovani.monetovanisrv.entity.financial.MarketQuotation;
import com.monetovani.monetovanisrv.entity.financial.keys.MarketDataKeys;
import com.monetovani.monetovanisrv.model.MarketDataCreationResponse;
import com.monetovani.monetovanisrv.repository.MarketQuotationRepository;
import com.monetovani.monetovanisrv.service.AssetService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.ExchangeStrategies;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

@Service
public class B3QuotationService {

    public enum FileDateType {
        YEAR("A"),
        MONTH("M"),
        DATE("D");

        private String fileDateType;

        FileDateType(String fileDateType) {
            this.fileDateType = fileDateType;
        }

        public String getFileDateType() {
            return fileDateType;
        }
    };

    @Autowired
    private Environment env;

    @Autowired
    private AssetService assetService;

    @Autowired
    private MarketQuotationRepository marketQuotationRepository;

    public MarketDataCreationResponse processQuotationRequest(MarketDataCreationResponse results,
            B3QuotationService.FileDateType datePrefix, String filePattern, boolean purge) {

        if (purge) {
            results.setQuotationPurgeCount(this.purgeMarketDataInDb(datePrefix, filePattern));
        }

        try {
            results.setQuotationCreationCount(this.extractQuotation(datePrefix, filePattern).size());
            results.setMsg("Success");
        } catch (DataIntegrityViolationException | WebClientResponseException e) {
            results.setQuotationCreationCount(0);
            results.setMsg(e.getLocalizedMessage());
        }

        return results;
    }

    public List<MarketQuotation> extractQuotation(FileDateType datePrefix, String filePattern) {
        List<MarketQuotation> addedQuotations = null;
        String baseUrl = env.getProperty("market-data-api-b3-url");

        ExchangeStrategies exchangeStrategies = ExchangeStrategies.builder()
                .codecs(configurer -> configurer.defaultCodecs().maxInMemorySize(1024 * 1024 * 50)).build();

        byte[] response = WebClient.builder()
                .baseUrl(baseUrl)
                .exchangeStrategies(exchangeStrategies)
                .build().get().uri(uriBuilder -> uriBuilder
                        .pathSegment("COTAHIST_" + datePrefix.getFileDateType() + filePattern + ".zip")
                        .build()).retrieve().bodyToMono(byte[].class).block();

        try {
            ZipInputStream zipIn = new ZipInputStream(new ByteArrayInputStream(response));
            ZipEntry entry = zipIn.getNextEntry();
            StringBuilder s = new StringBuilder();

            while (entry != null) {
                if (!entry.isDirectory() && entry.getName().equals("COTAHIST_" + datePrefix.getFileDateType() + filePattern + ".TXT")) {
                    byte[] buffer = new byte[1024];
                    int read = 0;
                    while ((read = zipIn.read(buffer, 0, 1024)) >= 0) {
                        s.append(new String(buffer, 0, read));
                    }
                }
                zipIn.closeEntry();
                entry = zipIn.getNextEntry();
            }
            addedQuotations = this.getQuotationFromResponseString(s.toString());
            zipIn.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return addedQuotations;
    }

    private List<MarketQuotation> getQuotationFromResponseString(String apiResponse) {
        List<MarketQuotation> foundMarketData = new ArrayList<>();
        HashMap<String, Asset> assetMap = new HashMap<>();
        assetService.getAllAssets().forEach(asset -> assetMap.put(asset.getCode(), asset));
        String[] lines = apiResponse.split("\r\n");

        for (int i=1; i<lines.length; i++) {
            String assetCode = lines[i].substring(12, 24).trim();
            if (assetMap.containsKey(assetCode)) {
                LocalDate date = LocalDate.parse(lines[i].substring(2, 10), DateTimeFormatter.BASIC_ISO_DATE);
                float openValue = Float.parseFloat(lines[i].substring(56, 69)) / 100;
                float maxValue = Float.parseFloat(lines[i].substring(69, 82)) / 100;
                float minValue = Float.parseFloat(lines[i].substring(82, 95)) / 100;
                float closeValue = Float.parseFloat(lines[i].substring(108, 121)) / 100;
                float volume = Float.parseFloat(lines[i].substring(170, 188)) / 100;
                MarketQuotation mktData = new MarketQuotation(
                        new MarketDataKeys(assetMap.get(assetCode), date), openValue, minValue, maxValue, closeValue, volume);
                foundMarketData.add(mktData);
            }
        }

        marketQuotationRepository.saveAllMarketQuotations(foundMarketData);
        return foundMarketData;
    }

    private int purgeMarketDataInDb(B3QuotationService.FileDateType datePrefix, String filePattern) {
        LocalDate startDate;
        LocalDate endDate;
        String completeStartDateString;
        String completeEndDateString;
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("ddMMyyyy");
        if (datePrefix.equals(B3QuotationService.FileDateType.DATE)) {
            completeStartDateString = filePattern;
            completeEndDateString = filePattern;
        } else if (datePrefix.equals(B3QuotationService.FileDateType.MONTH)) {
            completeStartDateString = "01" + filePattern;
            startDate = LocalDate.parse(completeStartDateString, formatter);
            int lastDayOfMonth = startDate.getMonth().length(startDate.isLeapYear());
            completeEndDateString = lastDayOfMonth + filePattern;
        } else {
            completeStartDateString = "0101" + filePattern;
            completeEndDateString = "3112" + filePattern;
        }
        startDate = LocalDate.parse(completeStartDateString, formatter);
        endDate = LocalDate.parse(completeEndDateString, formatter);
        return marketQuotationRepository.deleteByIdEventDateBetween(startDate, endDate);
    }
}
