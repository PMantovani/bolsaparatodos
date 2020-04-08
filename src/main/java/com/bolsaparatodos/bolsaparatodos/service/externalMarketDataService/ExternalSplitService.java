package com.bolsaparatodos.bolsaparatodos.service.externalMarketDataService;

import com.bolsaparatodos.bolsaparatodos.model.MarketDataCreationResponse;

import java.time.LocalDate;

public interface ExternalSplitService {

    MarketDataCreationResponse processSplitRequest(MarketDataCreationResponse results,
                                                   LocalDate startDate, LocalDate endDate, boolean purgeExistent);
}
