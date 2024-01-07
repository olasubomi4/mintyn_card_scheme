package com.mintyn.assessment.dto.cardverification.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CardStatsResponse {
    private Boolean success;
    private Integer start;
    private Integer limit;
    private Long size;
    private Map<String,String> payload;
}
