package com.monetovani.monetovanisrv.entity.financial;

import lombok.Data;

import javax.persistence.*;

@Data
@Entity
class Asset {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String code;

    @Enumerated(EnumType.STRING)
    private AssetType type;

    @Enumerated(EnumType.STRING)
    private TaxRule taxRule;

    public enum AssetType {
        FIXED_INCOME,
        INVESTMENT_FUND,
        REAL_STATE_FUND,
        SHARE,
        ETF
    }

    public enum TaxRule {
        NO_TAX,                 // No taxes on profits
        FIXED_15,               // 15% of taxes on profits
        REGRESSIVE_22_5_TO_15   // 22,5% until 6 months,
                                // 20% between 6 months and 1 year,
                                // 17,5% between 1 and 2 years,
                                // 15% over 2 years
    }
}
