package com.mintyn.assessment.dto.bin.responsev2;

import lombok.Data;

@Data
public class BinResponse {
    private NumberInfo number = new NumberInfo();
    private String scheme;
    private String type;
    private String brand;
    private boolean prepaid;
    private CountryInfo country= new CountryInfo();
    private BankInfo bank = new BankInfo();
}
