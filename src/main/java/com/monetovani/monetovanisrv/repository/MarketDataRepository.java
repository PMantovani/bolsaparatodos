package com.monetovani.monetovanisrv.repository;

import com.monetovani.monetovanisrv.entity.financial.MarketData;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface MarketDataRepository extends JpaRepository<MarketData, Long> {

    List<MarketData> findByIdAssetCodeAndIdEventDateBetweenOrderByIdEventDateDesc(
            String assetCode, LocalDate startDate, LocalDate endDate);

    List<MarketData> findByIdAssetCodeInAndIdEventDateBetweenOrderByIdAssetCodeAscIdEventDateDesc(
            List<String> assetCodes, LocalDate startDate, LocalDate endDate);
}
