package com.monetovani.monetovanisrv.model;


import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;

@Data
public class MarketDataByDate {

    @JsonFormat(pattern = "yyyy-MM-dd")
    LocalDate date;
    List<MarketDataModel> marketData;
}
