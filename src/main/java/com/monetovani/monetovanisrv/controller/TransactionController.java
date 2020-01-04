package com.monetovani.monetovanisrv.controller;

import com.monetovani.monetovanisrv.controller.exceptionhandler.TransactionNotFound;
import com.monetovani.monetovanisrv.entity.Transaction;
import com.monetovani.monetovanisrv.repository.TransactionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

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
}
