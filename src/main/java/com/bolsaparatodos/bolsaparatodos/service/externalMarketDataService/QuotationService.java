package com.bolsaparatodos.bolsaparatodos.service.externalMarketDataService;

import com.bolsaparatodos.bolsaparatodos.entity.financial.MarketQuotation;

import java.time.LocalDateTime;
import java.util.List;

public interface QuotationService {

    List<MarketQuotation> getQuotation(String assetCode, LocalDateTime startDate, LocalDateTime endDate);
}
