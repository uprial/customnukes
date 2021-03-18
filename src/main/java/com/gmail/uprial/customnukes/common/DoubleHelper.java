package com.gmail.uprial.customnukes.common;

/*
    Each float point value contains four parts in its representation:
    [<sign>]<left-part>[<point>[<right-path>]]

    Here we define some restrictions related to max size of left and right parts.
 */

public final class DoubleHelper {
    // This value is just picked by author.
    static final int MAX_RIGHT_SIZE = 4;

    // This value is calculated based on MAX_RIGHT_SIZE and capacity of Java’s double type.
    // See tests to understand how it works.
    static final int MAX_LEFT_SIZE = 11;

    public static final double MIN_DOUBLE_VALUE = Math.pow(10.0, -MAX_RIGHT_SIZE);

    public static final double MAX_DOUBLE_VALUE = Math.pow(10.0, MAX_LEFT_SIZE) - MIN_DOUBLE_VALUE;

    // Create a visible representation without needless digits after the point.
    public static String formatDoubleValue(double value) {
        return String.format("%." + getRightDigits(value, MAX_RIGHT_SIZE) + 'f', value);
    }

    // Check if a value has too many digits the left part.
    // In other words, the value is too big.
    public static boolean isLengthOfLeftPartOfDoubleGood(double value) {
        return getLeftDigits(value) <= MAX_LEFT_SIZE;
    }

    // Check if a value has too many digits in the right part.
    public static boolean isLengthOfRightPartOfDoubleGood(double value) {
        return getRightDigits(value, MAX_RIGHT_SIZE + 1) <= MAX_RIGHT_SIZE;
    }

    public static boolean isLengthOfDoubleGood(double value) {
        return isLengthOfLeftPartOfDoubleGood(value) && isLengthOfRightPartOfDoubleGood(value);
    }

    // #### protected functions ####

    static int getRightDigits(double value, int maxExponent) {
        int digits = 0;
        //noinspection MethodCallInLoopCondition
        while((Math.abs(round(value, digits) - value) > Double.MIN_VALUE) && (digits < maxExponent)) {
            digits += 1;
        }
        return digits;
    }

    static int getLeftDigits(double value) {
        return Math.max(0, (int)Math.floor(log(10.0, value))) + 1;
    }

    // Get a logarithm with a specific base
    static double log(double base, double value) {
        return Math.log(value) / Math.log(base);
    }

    // Round a value to a specific digit
    static double round(double value, int digit) {
        double multiplier = Math.pow(10.0, digit);

        return Math.round(value * multiplier) / multiplier;
    }
}
