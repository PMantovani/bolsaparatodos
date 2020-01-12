package com.monetovani.monetovanisrv.entity.financial;

import lombok.Data;

import javax.persistence.*;

@Data
@Entity
class Account {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @ManyToOne
    private User user;

    private String name;

    @Enumerated(EnumType.STRING)
    private AccountType type;

    public enum AccountType {
        CHECKING_ACCOUNT,
        SAVINGS_ACCOUNT,
        BROKER,
        CASH,
        OTHER
    }

}
