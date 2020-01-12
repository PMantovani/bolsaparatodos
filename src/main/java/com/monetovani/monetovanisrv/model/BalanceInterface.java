package com.monetovani.monetovanisrv.model;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.LocalDate;
import java.time.LocalDateTime;

public interface BalanceInterface {

    float getBalance();
    LocalDate getDate();
}
