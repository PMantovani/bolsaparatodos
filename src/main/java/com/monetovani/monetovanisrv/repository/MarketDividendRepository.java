package com.monetovani.monetovanisrv.repository;

import com.monetovani.monetovanisrv.entity.financial.MarketDividend;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface MarketDividendRepository extends JpaRepository<MarketDividend, Long> {


    List<MarketDividend> findByIdAssetCodeAndIdEventDateBetweenOrderByIdEventDateDesc(
            String assetCode, LocalDate startDate, LocalDate endDate);
}
