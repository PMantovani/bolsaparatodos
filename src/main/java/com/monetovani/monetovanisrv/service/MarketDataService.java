package com.monetovani.monetovanisrv.service;

import com.monetovani.monetovanisrv.entity.financial.MarketData;
import com.monetovani.monetovanisrv.entity.financial.MarketQuotation;
import com.monetovani.monetovanisrv.model.MarketDataByDate;
import com.monetovani.monetovanisrv.model.MarketDataModel;
import com.monetovani.monetovanisrv.model.MarketDataModelWithDate;
import com.monetovani.monetovanisrv.repository.MarketDataRepository;
import com.monetovani.monetovanisrv.service.externalMarketDataService.AlphaVantageQuotationService;
import com.monetovani.monetovanisrv.service.externalMarketDataService.B3QuotationService;
import com.monetovani.monetovanisrv.service.externalMarketDataService.YahooFinanceQuotationService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
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
                    md.getMaxValue(), md.getCloseValue(), md.getSplitFactor(), md.getDividendPerShare(), md.getVolume()));
        }
        return result;
    }

    public List<MarketDataModelWithDate> getQuotationInPeriod(String assetCode, LocalDate startDate, LocalDate endDate) {
        List<MarketData> marketData = this.marketDataRepository
                .findByIdAssetCodeAndIdEventDateBetweenOrderByIdEventDateDesc(assetCode, startDate, endDate);

        List<MarketDataModelWithDate> mdModel = new ArrayList<>();
        for (MarketData md: marketData) {
            MarketDataModelWithDate element = new MarketDataModelWithDate();
            BeanUtils.copyProperties(md, element);
            element.setCode(md.getId().getAsset().getCode());
            element.setDate(md.getId().getEventDate());
            mdModel.add(element);
        }
        return mdModel;
    }
}
