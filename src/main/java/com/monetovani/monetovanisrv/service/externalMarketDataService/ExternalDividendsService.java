package com.monetovani.monetovanisrv.service.externalMarketDataService;

import com.monetovani.monetovanisrv.model.MarketDataCreationResponse;

import java.time.LocalDate;

public interface ExternalDividendsService {

    MarketDataCreationResponse processDividendsRequest(MarketDataCreationResponse results,
                                                       LocalDate startDate, LocalDate endDate, boolean purgeExistent);

}
