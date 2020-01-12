package com.monetovani.monetovanisrv.controller;

import com.monetovani.monetovanisrv.controller.exceptionhandler.TransactionNotFound;
import com.monetovani.monetovanisrv.entity.financial.Transaction;
import com.monetovani.monetovanisrv.model.Balance;
import com.monetovani.monetovanisrv.model.BalanceInterface;
import com.monetovani.monetovanisrv.repository.TransactionRepository;
import com.monetovani.monetovanisrv.utils.NumberConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.ArrayList;
import java.util.List;

@RestController
public class TransactionController {

    @Autowired
    private TransactionRepository repository;

    @GetMapping("/transactions")
    public List<Transaction> getTransactions() {
        return repository.findAll();
    }

    @GetMapping("/transactions/{id}")
    public Transaction getTransaction(@PathVariable("id") Long id) {
        return repository.findById(id).orElseThrow(() -> new TransactionNotFound(id));
    }

    @GetMapping("/balance/{id}")
    public List<Balance> balance(@PathVariable("id") Long id) {
        List<Balance> finalBalance = new ArrayList<>();
        List<BalanceInterface> dailyBalances = repository.findDailyBalance();
        float summedBalance = 0;
        for (BalanceInterface dailyBalance : dailyBalances) {
            summedBalance += dailyBalance.getBalance();
            finalBalance.add(new Balance(NumberConverter.fixDecimals(summedBalance), dailyBalance.getDate()));
        }
        return finalBalance;
    }
}
