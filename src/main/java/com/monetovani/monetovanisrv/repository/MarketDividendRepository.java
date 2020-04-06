package com.monetovani.monetovanisrv.repository;

import com.monetovani.monetovanisrv.entity.financial.MarketDividend;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MarketDividendRepository extends JpaRepository<MarketDividend, Long> {
}
