package com.monetovani.monetovanisrv.entity.financial.keys;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.monetovani.monetovanisrv.entity.financial.TransactionHeader;
import lombok.Data;
import lombok.ToString;

import javax.persistence.Embeddable;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.ManyToOne;
import java.io.Serializable;

@Data
@Embeddable
public class TransactionItemKeys implements Serializable {

    @ManyToOne
    @JsonBackReference
    @ToString.Exclude
    public TransactionHeader header;

    @GeneratedValue(strategy = GenerationType.AUTO)
    public Integer itemSequence;

}
