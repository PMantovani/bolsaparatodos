package com.bolsaparatodos.bolsaparatodos.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.bolsaparatodos.bolsaparatodos.entity.financial.MarketData;
import lombok.Data;
import org.springframework.beans.BeanUtils;

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
    float adjustedCloseValue;
    float splitFactor;
    float dividendPerShare;
    float volume;
    float percentageDifference;

    public MarketDataModelWithDate(MarketData marketData) {
        BeanUtils.copyProperties(marketData, this);
    }
}
