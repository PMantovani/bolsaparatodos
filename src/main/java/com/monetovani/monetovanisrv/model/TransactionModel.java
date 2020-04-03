package com.monetovani.monetovanisrv.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.monetovani.monetovanisrv.entity.financial.TransactionHeader;
import lombok.Data;
import org.springframework.beans.BeanUtils;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
public class TransactionModel {

    private Long id;
    private String title;
    @JsonFormat(pattern="yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
    private LocalDateTime transactionDate;
    @JsonFormat(pattern="yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
    private LocalDateTime paymentDate;
    @JsonFormat(pattern="yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
    private LocalDateTime creationDate;
    @JsonFormat(pattern="yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
    private LocalDateTime lastModifiedDate;
    private List<TransactionItemModel> items;

    public TransactionModel(TransactionHeader transaction) {
        BeanUtils.copyProperties(transaction, this);
        this.items = new ArrayList<>();
        transaction.getItems().forEach(x -> this.items.add(new TransactionItemModel(x)));
    }

}
