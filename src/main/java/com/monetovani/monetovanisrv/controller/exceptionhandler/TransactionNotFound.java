package com.monetovani.monetovanisrv.controller.exceptionhandler;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value= HttpStatus.NOT_FOUND, reason = "Transaction not found")
public class TransactionNotFound extends RuntimeException {

    public TransactionNotFound(long id) {
        super("Transaction " + id + " not found");
    }

}
