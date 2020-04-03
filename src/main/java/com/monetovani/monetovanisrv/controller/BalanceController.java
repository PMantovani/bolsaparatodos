package com.monetovani.monetovanisrv.controller;

import com.monetovani.monetovanisrv.model.Balance;
import com.monetovani.monetovanisrv.security.MyUserDetails;
import com.monetovani.monetovanisrv.service.BalanceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.List;

@RestController
@RequestMapping("/balance")
public class BalanceController {

    @Autowired
    private BalanceService service;

    @GetMapping
    public List<Balance> balance(@AuthenticationPrincipal MyUserDetails user,
                                 @RequestParam(required = false) String startDate,
                                 @RequestParam(required = false) String endDate) {
        long userId = user.getId();

        // Default is between today and a year ago.
        LocalDateTime startDateFormatted;
        LocalDateTime endDateFormatted;
        try {
            startDateFormatted = startDate == null ? LocalDateTime.now().minusMonths(3) : LocalDateTime.parse(startDate);
        } catch (DateTimeParseException e) {
            startDateFormatted = LocalDateTime.now().minusMonths(3);
        }

        try {
            endDateFormatted = endDate == null ? LocalDateTime.now() : LocalDateTime.parse(endDate);
        } catch (DateTimeParseException e) {
            endDateFormatted = LocalDateTime.now();
        }

        return service.getBalance(userId, startDateFormatted, endDateFormatted);
    }
}
