package com.monetovani.monetovanisrv.entity.financial;

import lombok.Data;

import javax.persistence.*;

@Data
@Entity
class Position {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @ManyToOne
    private User user;

    @ManyToOne
    private Asset asset;

    private int quantity; // Depending on the Asset Type, purchases on position can be done with or without quantities.
    private float purchaseValue;

    @ManyToOne
    private Account account; // Account that is linked to this position (generally a Broker)
}
