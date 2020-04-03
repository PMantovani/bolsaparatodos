package com.monetovani.monetovanisrv.controller;

import com.monetovani.monetovanisrv.entity.financial.MarketData;
import com.monetovani.monetovanisrv.model.MarketDataCreationResponse;
import com.monetovani.monetovanisrv.service.MarketDataService;
import com.monetovani.monetovanisrv.service.ExternalQuotationService.B3QuotationService;
import com.monetovani.monetovanisrv.service.externalMarketDataService.YahooFinanceQuotationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/marketdata")
public class MarketDataController {

    @Autowired MarketDataService service;
    @Autowired private B3QuotationService b3QuotationService;
    @Autowired private YahooFinanceQuotationService yahooFinanceService;

    @GetMapping("/{code}")
    public List<MarketData> getMarketData(
            @PathVariable("code") String assetCode,
            @RequestParam(required = false) LocalDate startDate,
            @RequestParam(required = false) LocalDate endDate) {
        startDate = startDate == null ? LocalDate.now().minusMonths(1) : startDate;
        endDate = endDate == null ? LocalDate.now() : endDate;
        return service.getQuotationInPeriod(assetCode, startDate, endDate);
    }

    @PostMapping("/quotations/{year}")
    public MarketDataCreationResponse syncMarketData(@PathVariable String year,
                                                     @RequestParam(defaultValue = "false") boolean purge) {
        MarketDataCreationResponse response = new MarketDataCreationResponse();
        return b3QuotationService.processQuotationRequest(response, B3QuotationService.FileDateType.YEAR, year, purge);
    }

    @PostMapping("/quotations/{year}/{month}")
    public MarketDataCreationResponse syncMarketData(
            @PathVariable String year,
            @PathVariable String month,
            @RequestParam(defaultValue = "false") boolean purge) {
        month = month.length() == 1 ? "0" + month : month;
        MarketDataCreationResponse response = new MarketDataCreationResponse();
        return b3QuotationService.processQuotationRequest(response, B3QuotationService.FileDateType.MONTH, month + year, purge);
    }

    @PostMapping("/quotations/{year}/{month}/{date}")
    public MarketDataCreationResponse syncMarketData(
            @PathVariable String year,
            @PathVariable String month,
            @PathVariable String date,
            @RequestParam(defaultValue = "false") boolean purge) {
        month = month.length() == 1 ? "0" + month : month;
        date = date.length() == 1 ? "0" + date : date;
        MarketDataCreationResponse response = new MarketDataCreationResponse();
        return b3QuotationService.processQuotationRequest(response, B3QuotationService.FileDateType.DATE, date + month + year, purge);
    }

    @PostMapping("/splits")
    public MarketDataCreationResponse syncMarketSplitData(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @RequestParam(defaultValue = "false") boolean purgeExistent) {
        MarketDataCreationResponse response = new MarketDataCreationResponse();
        return yahooFinanceService.processSplitRequest(response, startDate, endDate, false);
    }

    @PostMapping("/dividends")
    public MarketDataCreationResponse syncMarketDividendData(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @RequestParam(defaultValue = "false") boolean purgeExistent) {
        MarketDataCreationResponse response = new MarketDataCreationResponse();
        return yahooFinanceService.processDividendsRequest(response, startDate, endDate, false);
    }
}
