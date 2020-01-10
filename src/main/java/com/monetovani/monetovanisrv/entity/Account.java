package com.monetovani.monetovanisrv.entity;

import lombok.Data;

import javax.persistence.*;

@Data
class Account {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "id")
    private User user;

    private String name;
    private AccountType type;

    public enum AccountType {
        CHECKING_ACCOUNT,
        SAVINGS_ACCOUNT,
        BROKER,
        CASH,
        OTHER
    }

}
