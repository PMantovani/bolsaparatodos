package com.bolsaparatodos.bolsaparatodos.entity.financial;

import com.bolsaparatodos.bolsaparatodos.entity.financial.keys.MarketDataKeys;
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
public class MarketSplit {

    @EmbeddedId
    MarketDataKeys id;

    float splitFactor;
    String splitRatio;
}
