package me.opkarol.oplibrary.tools;

import me.opkarol.oplibrary.injection.IgnoreInject;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;

@IgnoreInject
public class NumberFormatting {
    public static String formatNumber(double number) {
        DecimalFormatSymbols symbols = new DecimalFormatSymbols();
        symbols.setGroupingSeparator(',');
        symbols.setDecimalSeparator('.');

        DecimalFormat decimalFormat = new DecimalFormat("$#,##0.###", symbols);
        return decimalFormat.format(number);
    }
}