package com.mintyn.assessment.util;

public class Utility {
    public static String maskCardNumber(String cardNumber) {

        if (cardNumber == null || cardNumber.length() != 16 || !cardNumber.matches("\\d+")) {
            throw new IllegalArgumentException("Invalid PAN format");
        }

        String maskedCardNumber = cardNumber.substring(0, 6) + "******" + cardNumber.substring(12);

        return maskedCardNumber;
    }
}
