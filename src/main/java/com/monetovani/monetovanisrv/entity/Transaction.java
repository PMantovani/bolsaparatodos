package com.monetovani.monetovanisrv.entity;

import lombok.Data;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Data
@Table(name = "Transactions")
public class Transaction {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "id")
    private User user;

    private String title;
    private boolean isInvisible; // If it should appear in transaction feed. (i.e stock market changes shouldn't be shown)
    private boolean isAccountable; // If this transaction should be accounted in earnings/expenses. (i.e initial account balances)

    @ManyToOne
    @JoinColumn(name = "id")
    private Account fromAccount; // fromAccount null means it's an income

    @ManyToOne
    @JoinColumn(name = "id")
    private Account toAccount; // toAccount and toPosition null means it's an outcome

    @ManyToOne
    @JoinColumn(name = "id")
    private Position toPosition;

    @ManyToOne
    @JoinColumn(name = "id")
    private Category category;

    private float value;
    private LocalDateTime transactionDate;
    private LocalDateTime creationDate;
    private LocalDateTime lastModifiedDate;

}
