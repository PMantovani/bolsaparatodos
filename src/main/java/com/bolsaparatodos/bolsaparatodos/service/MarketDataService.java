package com.bolsaparatodos.bolsaparatodos.service;

import com.bolsaparatodos.bolsaparatodos.entity.financial.Asset;
import com.bolsaparatodos.bolsaparatodos.entity.financial.MarketData;
import com.bolsaparatodos.bolsaparatodos.entity.financial.MarketSplit;
import com.bolsaparatodos.bolsaparatodos.model.MarketDataModel;
import com.bolsaparatodos.bolsaparatodos.model.MarketDataModelWithDate;
import com.bolsaparatodos.bolsaparatodos.repository.MarketDataRepository;
import com.bolsaparatodos.bolsaparatodos.repository.MarketSplitRepository;
import com.bolsaparatodos.bolsaparatodos.model.MarketDataByDate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.groupingBy;

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
                .findByIdAssetCodeInAndIdEventDateBetweenOrderByIdAssetCodeAscIdEventDateDesc(assetCodes, startDate, endDate);

        return marketData
                .stream()
                .collect(groupingBy(i -> i.getId().getAsset())) // Group market data by different assets
                .values()
                .stream()
                .map(i -> this.performConversions(i, endDate)) // Adjust asset market data values
                .flatMap(List::stream) // Add all results by different assets together
                .collect(groupingBy(MarketDataModelWithDate::getDate)) // Group them by the same date
                .entrySet()
                .stream()
                .map(i -> {
                    // Convert MarketDataModelWithDate set to MarketDataModel list
                    MarketDataByDate item = new MarketDataByDate();
                    item.setDate(i.getKey());
                    item.setMarketData(
                            i.getValue()
                                    .stream()
                                    .map(MarketDataModel::new)
                                    .sorted(Comparator.comparing(MarketDataModel::getCode)) // Sort by asset code
                                    .collect(Collectors.toList())
                    );
                    return item;
                })
                .sorted(Comparator.comparing(MarketDataByDate::getDate).reversed()) // Sort by descending date order
                .collect(Collectors.toList());
    }

    public List<MarketDataModelWithDate> getQuotationInPeriod(String assetCode, LocalDate startDate, LocalDate endDate) {
        List<MarketData> marketData = this.marketDataRepository
                .findByIdAssetCodeAndIdEventDateBetweenOrderByIdEventDateDesc(assetCode, startDate, endDate);

        return this.performConversions(marketData, endDate);
    }

    private List<MarketDataModelWithDate> performConversions(List<MarketData> marketData, LocalDate endDate) {
        List<MarketDataModelWithDate> result = this.adjustClosingValue(marketData, endDate);
        return this.calculatePercentageDifference(result);
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
            MarketDataModelWithDate element = new MarketDataModelWithDate(md);
            element.setCode(md.getId().getAsset().getCode());
            element.setDate(md.getId().getEventDate());
            element.setAdjustedCloseValue((element.getCloseValue() * accumulatedSplitFactor * (1 - accumulatedDividendPerCurrencyUnit)));

            accumulatedDividendPerCurrencyUnit += element.getDividendPerShare() / element.getOpenValue();

            accumulatedSplitFactor *= element.getSplitFactor();
            mdModel.add(element);
        }
        return mdModel;
    }

    /**
     * Return list with filled percentageDifference field. That's the percentage difference of the closing value of the date
     * compared to the initial date closing value.
     * @param marketData initial marketData. Needs to be sorted in reversed order by date. (Most recent date first)
     * @return list with filled percentageDifference.
     */
    private List<MarketDataModelWithDate> calculatePercentageDifference(List<MarketDataModelWithDate> marketData) {
        if (marketData.isEmpty()) {
            return marketData;
        }

        float initialValue = marketData.get(marketData.size() - 1).getAdjustedCloseValue();
        for (MarketDataModelWithDate md: marketData) {
            md.setPercentageDifference(100 * (md.getAdjustedCloseValue() - initialValue) / initialValue);
        }
        return marketData;
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
