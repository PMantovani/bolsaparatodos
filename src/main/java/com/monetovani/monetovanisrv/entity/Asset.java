package com.monetovani.monetovanisrv.entity;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

class Asset {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String code;
    private AssetType type;

    public enum AssetType {
        FIXED_INCOME,
        INVESTMENT_FUND,
        REAL_STATE_FUND,
        SHARE,
        ETF
    }
}
