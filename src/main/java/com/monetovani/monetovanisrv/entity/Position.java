package com.monetovani.monetovanisrv.entity;

import lombok.Data;

import javax.persistence.*;

@Data
class Position {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "id")
    private User user;

    private Asset asset;
    private int quantity; // Depending on the Asset Type, purchases on position can be done with or without quantities.
    private float purchaseValue;
}
