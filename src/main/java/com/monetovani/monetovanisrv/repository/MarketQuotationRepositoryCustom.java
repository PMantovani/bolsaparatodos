package com.monetovani.monetovanisrv.repository;

import com.monetovani.monetovanisrv.entity.financial.MarketQuotation;

import java.util.List;

public interface MarketQuotationRepositoryCustom {
    void saveAllMarketQuotations(List<MarketQuotation> quotationList);
}
