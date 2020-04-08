package com.bolsaparatodos.bolsaparatodos.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.beans.BeanUtils;

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
    float percentageDifference;

    public MarketDataModel(MarketDataModelWithDate marketData) {
        BeanUtils.copyProperties(marketData, this);
    }
}
