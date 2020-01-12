package com.monetovani.monetovanisrv.entity.financial;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import org.springframework.lang.Nullable;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Data
public class Transaction {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @ManyToOne
    private User user;

    @Nullable
    private String title;
    private boolean isInvisible; // If it should appear in transaction feed. (i.e stock market changes shouldn't be shown)
    private boolean isAccountable; // If this transaction should be accounted in earnings/expenses. (i.e initial account balances and transfers)

    @ManyToOne
    @Nullable
    private Account fromAccount; // When withdrawing money from an account

    @ManyToOne
    @Nullable
    private Account toAccount; // When putting money in an account

    @ManyToOne
    @Nullable
    private CreditCard fromCreditCard;

    @ManyToOne
    @Nullable
    private CreditCard toCreditCard;

    @ManyToOne
    @Nullable
    private Position fromPosition; // Non-null when selling a position

    @ManyToOne
    @Nullable
    private Position toPosition; // Non-null when purchasing a position

    @ManyToOne
    @Nullable
    private Category category;

    private float value; // Values will always be positive in the backend

    @JsonFormat(pattern="yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
    private LocalDateTime transactionDate;
    @JsonFormat(pattern="yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
    private LocalDateTime paymentDate;
    @JsonFormat(pattern="yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
    private LocalDateTime creationDate;
    @JsonFormat(pattern="yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
    private LocalDateTime lastModifiedDate;

}
