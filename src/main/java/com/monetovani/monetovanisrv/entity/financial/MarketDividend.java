package com.monetovani.monetovanisrv.entity.financial;

import com.monetovani.monetovanisrv.entity.financial.keys.MarketDataKeys;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;

@Data
@Table
@Entity
@NoArgsConstructor
@AllArgsConstructor
public class MarketDividend {

    @EmbeddedId
    MarketDataKeys id;

    float dividendPerShare;
}
