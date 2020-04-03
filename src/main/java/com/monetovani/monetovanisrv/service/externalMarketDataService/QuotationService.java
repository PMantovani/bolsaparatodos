package com.monetovani.monetovanisrv.service.externalMarketDataService;

import com.monetovani.monetovanisrv.entity.financial.MarketQuotation;

import java.time.LocalDateTime;
import java.util.List;

public interface QuotationService {

    List<MarketQuotation> getQuotation(String assetCode, LocalDateTime startDate, LocalDateTime endDate);
}
