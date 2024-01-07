package com.mintyn.assessment.dto.cardverification.response;

import lombok.Builder;
import lombok.Data;
@Data
@Builder
public class CardVerificationPayload {

    private String scheme;
    private String type;
    private String bank;
}
