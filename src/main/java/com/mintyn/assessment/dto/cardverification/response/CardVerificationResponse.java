package com.mintyn.assessment.dto.cardverification.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CardVerificationResponse {
    private  boolean success;
    private  CardVerificationPayload payload;
}
