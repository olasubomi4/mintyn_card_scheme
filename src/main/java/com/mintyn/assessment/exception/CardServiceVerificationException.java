package com.mintyn.assessment.exception;

public class CardServiceVerificationException extends RuntimeException {

    public CardServiceVerificationException(String message){
        super(message);
    }

    public CardServiceVerificationException(String message, Throwable cause){
        super(message, cause);
    }
    
}