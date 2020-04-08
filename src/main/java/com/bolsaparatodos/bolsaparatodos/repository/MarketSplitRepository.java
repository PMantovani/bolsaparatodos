package com.bolsaparatodos.bolsaparatodos.repository;

import com.bolsaparatodos.bolsaparatodos.entity.financial.MarketSplit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface MarketSplitRepository extends JpaRepository<MarketSplit, Long> {

    List<MarketSplit> findByIdAssetCodeAndIdEventDateBetweenOrderByIdEventDateDesc(
            String assetCode, LocalDate startDate, LocalDate endDate);
}
