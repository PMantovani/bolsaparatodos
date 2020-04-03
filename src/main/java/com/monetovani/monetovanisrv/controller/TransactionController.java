package com.monetovani.monetovanisrv.controller;

import com.monetovani.monetovanisrv.controller.exceptionhandler.TransactionNotFound;
import com.monetovani.monetovanisrv.entity.financial.TransactionHeader;
import com.monetovani.monetovanisrv.model.TransactionModel;
import com.monetovani.monetovanisrv.repository.TransactionRepository;
import com.monetovani.monetovanisrv.security.MyUserDetails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
public class TransactionController {

    @Autowired
    private TransactionRepository repository;

    @GetMapping("/transactions")
    public List<TransactionModel> getTransactions(@AuthenticationPrincipal MyUserDetails user) {
        long id = user.getId();
        List<TransactionHeader> transactions = repository.findByUserId(id);
        return transactions.stream().map(TransactionModel::new).collect(Collectors.toList());
    }

    @GetMapping("/transactions/{id}")
    public TransactionHeader getTransaction(@AuthenticationPrincipal MyUserDetails user, @PathVariable("id") Long id) {
        long userId = user.getId();
        return repository.findByIdAndUserId(id, userId).orElseThrow(() -> new TransactionNotFound(id));
    }
}
