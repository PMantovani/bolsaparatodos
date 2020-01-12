package com.monetovani.monetovanisrv.utils;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;

public class NumberConverter {

    public static float fixDecimals(float number) {
        DecimalFormat df = new DecimalFormat();
        df.setMaximumFractionDigits(2);
        df.setDecimalFormatSymbols(new DecimalFormatSymbols(Locale.US));
        return Float.parseFloat(df.format(number));
    }
}
