package me.opkarol.oplibrary.tools;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;

public class NumberFormatting {
    public static String formatNumber(double number) {
        DecimalFormatSymbols symbols = new DecimalFormatSymbols();
        symbols.setGroupingSeparator(',');
        symbols.setDecimalSeparator('.');

        DecimalFormat decimalFormat = new DecimalFormat("$#,##0.###", symbols);
        return decimalFormat.format(number);
    }
}