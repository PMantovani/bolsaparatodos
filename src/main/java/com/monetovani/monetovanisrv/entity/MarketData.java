package com.monetovani.monetovanisrv.entity;

import lombok.Data;

import javax.persistence.*;
import java.time.LocalDateTime;

@Data
public class MarketData {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "id")
    private Asset asset;
    private LocalDateTime quotationDatetime;
    private float closeValue;
}
