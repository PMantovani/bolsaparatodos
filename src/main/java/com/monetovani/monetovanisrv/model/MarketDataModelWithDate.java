package com.monetovani.monetovanisrv.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.LocalDate;

@Data
public class MarketDataModelWithDate {

    @JsonFormat(pattern = "yyyy-MM-dd")
    LocalDate date;
    String code;
    float openValue;
    float minValue;
    float maxValue;
    float closeValue;
    float splitFactor;
    float dividendPerShare;
    float volume;
}
