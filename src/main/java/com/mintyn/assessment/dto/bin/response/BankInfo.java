package com.mintyn.assessment.dto.bin.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class BankInfo {
    private String name;
    private String url;
    private String phone;
    private String city;
}
