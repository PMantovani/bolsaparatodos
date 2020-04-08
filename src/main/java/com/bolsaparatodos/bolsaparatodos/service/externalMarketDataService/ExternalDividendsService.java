package com.bolsaparatodos.bolsaparatodos.service.externalMarketDataService;

import com.bolsaparatodos.bolsaparatodos.model.MarketDataCreationResponse;

import java.time.LocalDate;

public interface ExternalDividendsService {

    MarketDataCreationResponse processDividendsRequest(MarketDataCreationResponse results,
                                                       LocalDate startDate, LocalDate endDate, boolean purgeExistent);

}
