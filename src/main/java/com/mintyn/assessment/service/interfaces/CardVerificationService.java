package com.mintyn.assessment.service.interfaces;

import com.mintyn.assessment.dto.cardverification.response.CardStatsResponse;
import com.mintyn.assessment.dto.cardverification.response.CardVerificationResponse;

import java.util.Map;

public interface CardVerificationService {
    CardStatsResponse getCardVerificationStats(int start, int limit);
    CardVerificationResponse verifyCard (String cardNumber);
}
