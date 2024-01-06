package com.mintyn.assessment.service;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.mintyn.assessment.config.AppConfigurations;
import com.mintyn.assessment.dto.bin.responsev2.BinResponse;
import com.mintyn.assessment.dto.cardverification.response.CardStatsResponse;
import com.mintyn.assessment.dto.cardverification.response.CardVerificationPayload;
import com.mintyn.assessment.dto.cardverification.response.CardVerificationResponse;
import com.mintyn.assessment.entity.CardVerification;
import com.mintyn.assessment.exception.CardServiceVerificationException;
import com.mintyn.assessment.repository.CardVerificationRepository;
import com.mintyn.assessment.util.ResponseMessages;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.webjars.NotFoundException;

import java.time.Duration;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Service
@Log4j2
public class CardVerificationServiceImpl implements CardVerificationService{
    @Autowired
    private AppConfigurations appConfigurations;

    @Autowired
    private WebClient webClient;
    @Autowired
    private CardVerificationRepository cardVerificationRepository;


    private Cache<String, CardVerification> cardVerificationCache;


    public CardVerificationServiceImpl() {
        this.cardVerificationCache = Caffeine.newBuilder()
                .expireAfterWrite(1, TimeUnit.HOURS) // Adjust expiration time as needed
                .build();
    }

    @Override
    public CardStatsResponse getCardVerificationStats(int start, int limit) {
        Pageable pageable= PageRequest.of(start-1,limit);

        Page<CardVerification> cardVerifications = cardVerificationRepository.findAll(pageable);
        return CardStatsResponse.builder().success(true).start(start).limit(limit).size(cardVerifications.getTotalElements()).payload(getCardNumberToHitCountAsMap(cardVerifications)).build();
    }

    @Override
    public CardVerificationResponse verifyCard(String cardNumber) {
        cardNumber= modifyCardNumber(cardNumber);
        // Check if card verification details are in cache
        CardVerification cardVerificationDetails = cardVerificationCache.getIfPresent(cardNumber);
        if(cardVerificationDetails!=null)
        {
            log.info("Retrieving bin details from cache");
            increaseCardVerificationHitCountBy1(cardVerificationDetails);
            CardVerificationPayload cardVerificationPayload= CardVerificationPayload.builder().scheme(cardVerificationDetails.getScheme()).type(cardVerificationDetails.getType()).bank(cardVerificationDetails.getBank()).build();
            return CardVerificationResponse.builder().success(true).payload(cardVerificationPayload).build();
        }

        // Check if card verification details are in the database
        cardVerificationDetails= getBinDetailsFromDatabase(cardNumber);
        if(cardVerificationDetails!=null)
        {
            // Save to the database and cache
            cardVerificationCache.put(cardNumber,cardVerificationDetails);
            increaseCardVerificationHitCountBy1(cardVerificationDetails);
            CardVerificationPayload cardVerificationPayload= CardVerificationPayload.builder().scheme(cardVerificationDetails.getScheme()).type(cardVerificationDetails.getType()).bank(cardVerificationDetails.getBank()).build();
            return CardVerificationResponse.builder().success(true).payload(cardVerificationPayload).build();
        }

        // Fetch details from the api
        BinResponse binResponse= getBinDetails(cardNumber);
        if(isCardDetailsNotFoundInApiResponse(binResponse))
        {
            throw new NotFoundException(ResponseMessages.CARD_DETAILS_NOT_FOUND.getResponseMessage());
        }

        String cardScheme=binResponse.getScheme();
        String cardType= binResponse.getType();
        String issuerName= binResponse.getBank().getName();

        // Save to the database and cache
        cardVerificationDetails= CardVerification.builder().cardNumber(cardNumber).scheme(cardScheme)
                .type(cardType).bank(issuerName).hitCount(1).build();
        cardVerificationCache.put(cardNumber,cardVerificationDetails);
        cardVerificationRepository.save(cardVerificationDetails);
        CardVerificationPayload cardVerificationPayload= CardVerificationPayload.builder().scheme(cardScheme).type(cardType).bank(issuerName).build();
        return CardVerificationResponse.builder().success(true).payload(cardVerificationPayload).build();
    }

    private Map<String,String> getCardNumberToHitCountAsMap(Page<CardVerification> cardVerifications)
    {
       return cardVerifications.stream()
                .collect(Collectors.toMap(CardVerification::getCardNumber, verification -> String.valueOf(verification.getHitCount())));
    }

    private CardVerification getBinDetailsFromDatabase(String cardNumber)
    {
        log.info("Retrieving bin details from database");
        return cardVerificationRepository.findByCardNumber(cardNumber);
    }
    private BinResponse getBinDetails(String bin)
    {
       log.info("Retrieving bin details from 3rd party api");
       try {
           ResponseEntity<BinResponse> response = webClient.mutate().baseUrl(appConfigurations.getBinDetailApiUrl())
                   .build().get().uri(bin).retrieve().toEntity(BinResponse.class).timeout(Duration.ofSeconds(10))
                   .block();
           if (response != null) {
               log.info("Raw Response from 3rd party API: {}", response.toString());

               if (response.getStatusCode() == HttpStatus.OK) {
                   return response.getBody();
               }
           }
           throw new CardServiceVerificationException(ResponseMessages.SERVICE_IS_UNAVAILABLE.getResponseMessage());
       }
       catch (Exception ex)
       {
           log.error("Error while fetching bin details from the API: {}", ex.getMessage());
           throw new CardServiceVerificationException(ResponseMessages.SERVICE_IS_UNAVAILABLE.getResponseMessage());
       }
    }

    private void increaseCardVerificationHitCountBy1(CardVerification cardVerification)
    {
        cardVerification.setHitCount(cardVerification.getHitCount()+1);
        cardVerificationRepository.save(cardVerification);
    }

    private Boolean isCardDetailsNotFoundInApiResponse(BinResponse binResponse)
    {
        if(binResponse==null)
        {
            return true;
        }
        if(binResponse.getNumber()==null|| binResponse.getScheme()==null||binResponse.getType()==null)
        {
            return true;
        }
        return false;
    }

    private String modifyCardNumber(String cardNumber)
    {
        return cardNumber.substring(0,6);
    }

}
