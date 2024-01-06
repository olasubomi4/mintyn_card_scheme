package com.mintyn.assessment.util;

public enum ResponseMessages {
    SERVICE_IS_UNAVAILABLE("Card details could not be retrieved at the moment please try again later"),
    CARD_DETAILS_NOT_FOUND("Card details not found"),

    USER_ALREADY_EXIST("User with the provided information already exists. Please choose a different username");

    private String responseMessage;

    ResponseMessages(String responseMessage) {

        this.responseMessage = responseMessage;
    }



    public String getResponseMessage() {
        return responseMessage;
    }


}
