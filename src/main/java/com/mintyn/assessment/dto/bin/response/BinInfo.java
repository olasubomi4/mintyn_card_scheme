package com.mintyn.assessment.dto.bin.response;

import lombok.Data;

@Data
public class BinInfo {
    private boolean valid;
    private int number;
    private int length;
    private String scheme;
    private String brand;
    private String type;
    private String level;
    private String currency;
    private IssuerInfo issuer= new IssuerInfo();
    private CountryInfo country= new CountryInfo();
}
