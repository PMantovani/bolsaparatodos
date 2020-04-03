package com.monetovani.monetovanisrv.service;

import com.monetovani.monetovanisrv.entity.financial.MarketData;
import com.monetovani.monetovanisrv.entity.financial.MarketQuotation;
import com.monetovani.monetovanisrv.model.MarketDataByDate;
import com.monetovani.monetovanisrv.model.MarketDataModel;
import com.monetovani.monetovanisrv.repository.MarketDataRepository;
import com.monetovani.monetovanisrv.service.externalMarketDataService.AlphaVantageQuotationService;
import com.monetovani.monetovanisrv.service.externalMarketDataService.B3QuotationService;
import com.monetovani.monetovanisrv.service.externalMarketDataService.YahooFinanceQuotationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Service
public class MarketDataService {

    @Autowired
    private MarketDataRepository marketDataRepository;

    @Autowired
    private Environment env;

    @Autowired private AlphaVantageQuotationService alphaVantageQuotationService;
    @Autowired private YahooFinanceQuotationService yahooFinanceQuotationService;
    @Autowired private B3QuotationService b3QuotationService;

    public List<MarketDataByDate> getQuotationInPeriod(List<String> assetCodes, LocalDate startDate, LocalDate endDate) {
        List<MarketData> marketData = this.marketDataRepository
                .findByIdAssetCodeInAndIdEventDateBetweenOrderByIdEventDateDesc(assetCodes, startDate, endDate);

        List<MarketDataByDate> result = new ArrayList<>();
        MarketDataByDate mdInDate = null;
        for (MarketData md: marketData) {
            if (mdInDate == null || !mdInDate.getDate().equals(md.getId().getEventDate())) {
                if (mdInDate != null) {
                    result.add(mdInDate);
                }
                mdInDate = new MarketDataByDate();
                mdInDate.setDate(md.getId().getEventDate());
            }

            List<MarketDataModel> list = mdInDate.getMarketData();
            if (list == null) {
                list = new ArrayList<>();
                mdInDate.setMarketData(list);
            }
            list.add(new MarketDataModel(md.getId().getAsset().getCode(), md.getOpenValue(), md.getMinValue(),
                    md.getMaxValue(), md.getCloseValue(), md.getSplitFactor(), md.getDividendPerShare()));
        }
        return result;
    }

    public List<MarketData> getQuotationInPeriod(String assetCode, LocalDate startDate, LocalDate endDate) {
        return this.marketDataRepository.findByIdAssetCodeAndIdEventDateBetween(assetCode, startDate, endDate);
    }

    public List<MarketQuotation> getQuotationFromExternalApi(String assetCode, LocalDateTime startDate, LocalDateTime endDate) {
        String apiSource = env.getProperty("market-data-api-source");

        if (apiSource.equals("alphavantage")) {
            return alphaVantageQuotationService.getQuotation(assetCode, startDate, endDate);
        } else if (apiSource.equals("yahoofinance")) {
            return yahooFinanceQuotationService.getQuotation(assetCode, startDate, endDate);
        } else if (apiSource.equals("B3")) {
            return null;
//            return b3QuotationService.getQuotation();
        } else {
            return null;
        }
    }



}
