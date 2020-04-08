package com.bolsaparatodos.bolsaparatodos.repository;

import com.bolsaparatodos.bolsaparatodos.entity.financial.MarketDividend;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MarketDividendRepository extends JpaRepository<MarketDividend, Long> {
}
