package com.monetovani.monetovanisrv.model;

import com.monetovani.monetovanisrv.entity.financial.TransactionItem;
import lombok.Data;
import org.springframework.beans.BeanUtils;

@Data
public class TransactionItemModel {

    private Integer itemSequence;
    private boolean isInvisible;
    private float value;

    public TransactionItemModel(TransactionItem item) {
        BeanUtils.copyProperties(item, this);
        this.itemSequence = item.getId().getItemSequence();
    }

}
