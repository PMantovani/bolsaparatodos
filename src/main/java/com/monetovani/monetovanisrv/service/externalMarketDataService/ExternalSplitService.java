package com.monetovani.monetovanisrv.service.externalMarketDataService;

import com.monetovani.monetovanisrv.model.MarketDataCreationResponse;

import java.time.LocalDate;

public interface ExternalSplitService {

    MarketDataCreationResponse processSplitRequest(MarketDataCreationResponse results,
                                                   LocalDate startDate, LocalDate endDate, boolean purgeExistent);
}
