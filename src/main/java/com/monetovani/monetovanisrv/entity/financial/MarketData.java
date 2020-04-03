package com.monetovani.monetovanisrv.entity.financial;

import com.monetovani.monetovanisrv.entity.financial.keys.MarketDataKeys;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Immutable;

import javax.persistence.*;

@Data
@Entity
@Immutable
@NoArgsConstructor
@AllArgsConstructor
public class MarketData {

    @EmbeddedId
    MarketDataKeys id;

    float openValue;
    float minValue;
    float maxValue;
    float closeValue;
    float split_factor;
    float dividend_per_share;
}