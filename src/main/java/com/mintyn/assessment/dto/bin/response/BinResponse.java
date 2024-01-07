package com.mintyn.assessment.dto.bin.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class BinResponse {
    private NumberInfo number = new NumberInfo();
    private String scheme;
    private String type;
    private String brand;
    private boolean prepaid;
    private CountryInfo country= new CountryInfo();
    private BankInfo bank = new BankInfo();
}
