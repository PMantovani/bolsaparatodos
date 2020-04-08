package com.bolsaparatodos.bolsaparatodos.entity.financial;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Data
@Entity
@Table
@AllArgsConstructor
@NoArgsConstructor
public class Asset {

    @Id
    private String code;

    @Enumerated(EnumType.STRING)
    private AssetType type;

    public enum AssetType {
        REAL_STATE_FUND,
        SHARE,
        ETF,
        BDR,
        INDEX
    }

    private String name;

    public boolean isOddLot() {
        return this.getCode().endsWith("F");
    }
}
