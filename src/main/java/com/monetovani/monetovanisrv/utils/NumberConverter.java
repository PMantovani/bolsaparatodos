package com.monetovani.monetovanisrv.utils;

public class NumberConverter {

    public static float fixDecimals(float number) {
        String strFormat = String.format(java.util.Locale.US, "%.2f", number);
        return Float.parseFloat(strFormat);
    }
}
