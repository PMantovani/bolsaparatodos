package com.bolsaparatodos.bolsaparatodos.model;

import lombok.Data;

@Data
public class MarketDataCreationResponse {

    int quotationCreationCount;
    int quotationPurgeCount;
    int dividendCreationCount;
    int dividendPurgeCount;
    int splitCreationCount;
    int splitPurgeCount;
    String msg;
}
