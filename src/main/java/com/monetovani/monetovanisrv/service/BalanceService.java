package com.monetovani.monetovanisrv.service;

import com.monetovani.monetovanisrv.entity.financial.MarketData;
import com.monetovani.monetovanisrv.entity.financial.TransactionHeader;
import com.monetovani.monetovanisrv.model.AssetQuantity;
import com.monetovani.monetovanisrv.model.Balance;
import com.monetovani.monetovanisrv.repository.TransactionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class BalanceService {

    @Autowired
    private TransactionRepository transactionRepository;

    @Autowired
    private MarketDataService marketDataService;

    public List<Balance> getBalance(long userId, LocalDateTime startDate, LocalDateTime endDate) {
        LocalDate nextDateInBalance = startDate.toLocalDate();

        List<Balance> finalBalance = new ArrayList<>();
        List<TransactionHeader> transactions =
                transactionRepository.findByUserIdAndPaymentDateBetweenOrderByPaymentDate(userId, startDate, endDate);

        List<AssetQuantity> assetQuantitiesList = transactionRepository.findAssetsQuantitiesUntilDate(userId, startDate.toString());
        List<AssetQuantity> assetQtyAfterAllTransactions =
                transactionRepository.findAssetsQuantitiesUntilDate(userId, endDate.toString());

        // Translate result to a hashmap
        HashMap<String, Float> assetQuantities = new HashMap<>();
        assetQuantitiesList.forEach(i -> {
            float newValue = assetQuantities.getOrDefault(i.getAssetCode(), 0f) + i.getQuantity();
            assetQuantities.put(i.getAssetCode(), newValue);
        });

        // Get distinct assetIds from all the transactions
        Collection<String> assetCodes =
                assetQtyAfterAllTransactions.stream().map(AssetQuantity::getAssetCode).collect(Collectors.toList());

        List<MarketData> marketData = marketDataService.getQuotationInPeriod(assetCodes, startDate.toLocalDate().minusDays(4), endDate.toLocalDate());
        // Sort by ascending quotation date
        marketData.sort(Comparator.comparing(i -> i.getId().getEventDate()));

        Map<LocalDate, List<MarketData>> groupByMarketData =
                marketData.stream().collect(Collectors.groupingBy(i -> i.getId().getEventDate()));

        Iterator<TransactionHeader> transactionIterator = transactions.iterator();
        TransactionHeader nextTransaction = transactionIterator.hasNext() ? transactionIterator.next() : null;
        List<MarketData> marketDataInDate = this.findClosestMarketDataToDate(groupByMarketData, nextDateInBalance);
        List<MarketData> lastMarketData = marketDataInDate;

        while(nextDateInBalance.isBefore(endDate.plusDays(1).toLocalDate())) {

            // Sum all transaction up to this date to the asset quantities
            while (nextTransaction != null &&
                ( nextTransaction.getPaymentDate().toLocalDate().isBefore(nextDateInBalance) ||
                    nextTransaction.getPaymentDate().toLocalDate().isEqual(nextDateInBalance))) {

                nextTransaction.getItems().forEach(i -> {
                    float newValue = assetQuantities.getOrDefault(i.getAsset().getCode(), 0f) + i.getQuantity();
                    assetQuantities.put(i.getAsset().getCode(), newValue);
                });
                nextTransaction = transactionIterator.hasNext() ? transactionIterator.next() : null;
            }

            marketDataInDate = groupByMarketData.get(nextDateInBalance) != null ?
                    groupByMarketData.get(nextDateInBalance) : lastMarketData;

            List<MarketData> marketDataInDateComplete = marketDataInDate;

            // Fill missing quotations in current date for any asset with last quotation date
            for (MarketData mk: lastMarketData) {
                boolean containsMarketData = marketDataInDate.stream()
                        .anyMatch(i -> i.getId().getAsset().equals(mk.getId().getAsset()));
                if (!containsMarketData) {
                    marketDataInDateComplete.add(mk);
                }
            }
            marketDataInDate = marketDataInDateComplete;

            // Calculate total balance in day
            float totalBalanceInDay = 0;
            for (Map.Entry<String, Float> entry : assetQuantities.entrySet()) {
                float closeValue = marketDataInDate.stream()
                    .filter(i -> i.getId().getAsset().getCode().equals(entry.getKey()))
                    .findAny()
                    .get().getCloseValue();
                totalBalanceInDay += (entry.getValue() * closeValue);
            }

            finalBalance.add(new Balance(totalBalanceInDay, nextDateInBalance));
            nextDateInBalance = nextDateInBalance.plusDays(1);
            lastMarketData = marketDataInDate;
        }

        return finalBalance;
    }

    private List<MarketData> findClosestMarketDataToDate(Map<LocalDate, List<MarketData>> groupByMarketData, LocalDate startDate) {
        // Get closest marketDate to startDate. Try up to 4 days behind.
        if (groupByMarketData.get(startDate) != null) {
            return groupByMarketData.get(startDate);
        } else if (groupByMarketData.get(startDate.minusDays(1)) != null) {
            return groupByMarketData.get(startDate.minusDays(1));
        } else if (groupByMarketData.get(startDate.minusDays(2)) != null) {
            return groupByMarketData.get(startDate.minusDays(2));
        } else if (groupByMarketData.get(startDate.minusDays(3)) != null) {
            return groupByMarketData.get(startDate.minusDays(3));
        } else {
            return groupByMarketData.get(startDate.minusDays(4));
        }
    }
}
