package com.simple.bank.utlility;

public class ConfigUtils {
    // extract boolean parameter
    public static boolean toBoolean(String value, boolean defaultValue) {
        if (value == null) return defaultValue;
        return "true".equalsIgnoreCase(value);
    }

    // extract int parameter
    public static int toInt(String value, int defaultValue) {
        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }
}