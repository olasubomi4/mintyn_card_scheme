package com.mintyn.assessment.dto.bin.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class BinResponse {
    private boolean success;
    private Integer code;
    @JsonProperty("BIN")
    private BinInfo BIN = new BinInfo();
    private String result;
    private String message;
}
