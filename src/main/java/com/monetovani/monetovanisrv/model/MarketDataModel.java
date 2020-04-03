package com.monetovani.monetovanisrv.model;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class MarketDataModel {

    String code;
    float openValue;
    float minValue;
    float maxValue;
    float closeValue;
    float adjustedCloseValue;
    float splitFactor;
    float dividendPerShare;
    float volume;
}
