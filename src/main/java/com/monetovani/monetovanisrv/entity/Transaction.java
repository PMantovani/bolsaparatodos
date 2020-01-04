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

    private float value;

    @Column
    private String title;
    private Long user;

    private String fromAccount;
    private String toAccount;
    private String category;
    private LocalDateTime transactionDate;
    private LocalDateTime creationDate;
    private LocalDateTime lastModifiedDate;

}
