package com.monetovani.monetovanisrv.entity.financial;

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
        FIXED_INCOME,
        INVESTMENT_FUND,
        REAL_STATE_FUND,
        SHARE,
        ETF
    }

    private String name;

    public boolean isOddLot() {
        return this.getCode().endsWith("F");
    }
}
