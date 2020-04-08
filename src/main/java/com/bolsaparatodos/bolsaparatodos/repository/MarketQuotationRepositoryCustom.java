package com.bolsaparatodos.bolsaparatodos.repository;

import com.bolsaparatodos.bolsaparatodos.entity.financial.MarketQuotation;

import java.util.List;

public interface MarketQuotationRepositoryCustom {
    void saveAllMarketQuotations(List<MarketQuotation> quotationList);
}
