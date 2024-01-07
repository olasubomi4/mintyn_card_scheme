package com.mintyn.assessment.services.interfaces;

import com.mintyn.assessment.dto.cardverification.response.CardStatsResponse;
import com.mintyn.assessment.dto.cardverification.response.CardVerificationResponse;

public interface CardVerificationService {
    CardStatsResponse getCardVerificationStats(int start, int limit);
    CardVerificationResponse verifyCard (String cardNumber);
}
