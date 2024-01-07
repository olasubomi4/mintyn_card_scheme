package com.mintyn.assessment.dto.bin.response;

import lombok.Data;

@Data
public class CountryInfo {
    private String numeric;
    private String alpha2;
    private String name;
    private String emoji;
    private String currency;
    private double latitude;
    private double longitude;

}
