package com.bolsaparatodos.bolsaparatodos.repository;

import com.bolsaparatodos.bolsaparatodos.entity.financial.MarketQuotation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

public interface MarketQuotationRepository extends JpaRepository<MarketQuotation, Long>, MarketQuotationRepositoryCustom {

    @Transactional
    int deleteByIdEventDateBetween(LocalDate startDate, LocalDate endDate);
}
