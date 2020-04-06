package com.monetovani.monetovanisrv.service;

import com.monetovani.monetovanisrv.entity.financial.Asset;
import com.monetovani.monetovanisrv.entity.financial.MarketData;
import com.monetovani.monetovanisrv.entity.financial.MarketDividend;
import com.monetovani.monetovanisrv.entity.financial.MarketSplit;
import com.monetovani.monetovanisrv.model.MarketDataByDate;
import com.monetovani.monetovanisrv.model.MarketDataModel;
import com.monetovani.monetovanisrv.model.MarketDataModelWithDate;
import com.monetovani.monetovanisrv.repository.MarketDataRepository;
import com.monetovani.monetovanisrv.repository.MarketSplitRepository;
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
    private MarketSplitRepository marketSplitRepository;

    @Autowired
    private Environment env;

    public List<MarketDataByDate> getQuotationInPeriod(List<String> assetCodes, LocalDate startDate, LocalDate endDate) {
        List<MarketData> marketData = this.marketDataRepository
                .findByIdAssetCodeInAndIdEventDateBetweenOrderByIdEventDateDesc(assetCodes, startDate, endDate);

        List<MarketDataModelWithDate> marketDataAdjusted = this.adjustClosingValue(marketData, endDate);

        List<MarketDataByDate> result = new ArrayList<>();
        MarketDataByDate mdInDate = null;
        for (MarketDataModelWithDate md: marketDataAdjusted) {
            if (mdInDate == null || !mdInDate.getDate().equals(md.getDate())) {
                if (mdInDate != null) {
                    result.add(mdInDate);
                }
                mdInDate = new MarketDataByDate();
                mdInDate.setDate(md.getDate());
            }

            List<MarketDataModel> list = mdInDate.getMarketData();
            if (list == null) {
                list = new ArrayList<>();
                mdInDate.setMarketData(list);
            }
            list.add(new MarketDataModel(md.getCode(), md.getOpenValue(), md.getMinValue(),
                    md.getMaxValue(), md.getCloseValue(), md.getAdjustedCloseValue(),
                    md.getSplitFactor(), md.getDividendPerShare(), md.getVolume()));
        }
        return result;
    }

    public List<MarketDataModelWithDate> getQuotationInPeriod(String assetCode, LocalDate startDate, LocalDate endDate) {
        List<MarketData> marketData = this.marketDataRepository
                .findByIdAssetCodeAndIdEventDateBetweenOrderByIdEventDateDesc(assetCode, startDate, endDate);

        return this.adjustClosingValue(marketData, endDate);
    }

    private List<MarketDataModelWithDate> adjustClosingValue(List<MarketData> marketDataList, LocalDate endDate) {
        float accumulatedSplitFactor = 1;
        float accumulatedDividendPerCurrencyUnit = 0;

        if (!marketDataList.isEmpty()) {
            Asset asset = marketDataList.get(0).getId().getAsset();
            accumulatedSplitFactor = this.getAccumulatedSplitFactorFromEndDateUntilToday(asset, endDate);
            accumulatedDividendPerCurrencyUnit = this.getAccumulatedDividendsFromEndDateUntilToday(asset, endDate);
        }
        List<MarketDataModelWithDate> mdModel = new ArrayList<>();
        for (MarketData md: marketDataList) {
            MarketDataModelWithDate element = new MarketDataModelWithDate();
            BeanUtils.copyProperties(md, element);
            element.setCode(md.getId().getAsset().getCode());
            element.setDate(md.getId().getEventDate());
            element.setAdjustedCloseValue((element.getCloseValue() * accumulatedSplitFactor * (1 - accumulatedDividendPerCurrencyUnit)));

            accumulatedDividendPerCurrencyUnit += element.getDividendPerShare() / element.getOpenValue();

            accumulatedSplitFactor *= element.getSplitFactor();
            mdModel.add(element);
        }
        return mdModel;
    }

    private float getAccumulatedSplitFactorFromEndDateUntilToday(Asset asset, LocalDate endDate) {
        LocalDate afterEndDate = endDate.plusDays(1);
        List<MarketSplit> splits = this.marketSplitRepository.findByIdAssetCodeAndIdEventDateBetweenOrderByIdEventDateDesc(
                asset.getCode(), afterEndDate, LocalDate.now());
        float accumulatedSplit = 1;
        for (MarketSplit split : splits) {
            accumulatedSplit *= split.getSplitFactor();
        }
        return accumulatedSplit;
    }


    private float getAccumulatedDividendsFromEndDateUntilToday(Asset asset, LocalDate endDate) {
        LocalDate afterEndDate = endDate.plusDays(1);
        List<MarketData> data = this.marketDataRepository.findByIdAssetCodeAndIdEventDateBetweenOrderByIdEventDateDesc(
                asset.getCode(), afterEndDate, LocalDate.now());
        float accumulatedDividends = 0;
        for (MarketData marketData: data) {
            accumulatedDividends += marketData.getDividendPerShare() / marketData.getOpenValue();
        }
        return accumulatedDividends;
    }
}
