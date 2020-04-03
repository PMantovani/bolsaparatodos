package com.monetovani.monetovanisrv.entity.financial;

import com.monetovani.monetovanisrv.entity.financial.keys.TransactionItemKeys;
import lombok.Data;

import javax.persistence.*;

@Entity
@Data
@Table
public class TransactionItem {

    @EmbeddedId
    private TransactionItemKeys id;

    private boolean isInvisible;

    @ManyToOne
    private Asset asset;

    private int quantity;

    private float value; // Values will always be positive in the backend

}
