package com.mintyn.assessment.dto.bin.response;

import lombok.Data;

@Data
public class CountryInfo {
    private String name;
    private String nativeName;
    private String flag;
    private String numeric;
    private String capital;
    private String currency;
    private String currencySymbol;
    private String region;
    private String subregion;
    private String idd;
    private String alpha2;
    private String alpha3;
    private String language;
    private String languageCode;
    private double latitude;
    private double longitude;

}
