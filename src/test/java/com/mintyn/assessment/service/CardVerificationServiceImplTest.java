//package com.mintyn.assessment.service;
//
//import com.github.benmanes.caffeine.cache.Cache;
//import com.github.benmanes.caffeine.cache.Caffeine;
//import com.mintyn.assessment.config.AppConfigurations;
//import com.mintyn.assessment.dto.bin.responsev2.BinResponse;
//import com.mintyn.assessment.dto.cardverification.response.CardStatsResponse;
//import com.mintyn.assessment.dto.cardverification.response.CardVerificationResponse;
//import com.mintyn.assessment.entity.CardVerification;
//import com.mintyn.assessment.repository.CardVerificationRepository;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//import org.mockito.Mockito;
//import org.mockito.MockitoAnnotations;
//import org.springframework.data.domain.Page;
//import org.springframework.data.domain.PageRequest;
//import org.springframework.http.ResponseEntity;
//import static org.mockito.ArgumentMatchers.anyString;
//
//import org.springframework.web.reactive.function.client.WebClient;
//import org.springframework.web.reactive.function.client.WebClientResponseException;
//import org.webjars.NotFoundException;
//
//import java.util.Collections;
//import java.util.Optional;
//import java.util.concurrent.TimeUnit;
//
//import static org.junit.jupiter.api.Assertions.assertEquals;
//import static org.mockito.ArgumentMatchers.any;
//import static org.mockito.ArgumentMatchers.anyString;
//import static org.mockito.Mockito.when;
//
//class CardVerificationServiceImplTest {
//
//    @Mock
//    private AppConfigurations appConfigurations;
//
//    @Mock
//    private Cache<String, CardVerification> cardVerificationCache;
//
//    @Mock
//    private WebClient webClient;
//
//    @Mock
//    private WebClient.Builder webClientBuilder;
//
//    @Mock
//    private CardVerificationRepository cardVerificationRepository;
//
//    @InjectMocks
//    private CardVerificationServiceImpl cardVerificationService;
//
//    @BeforeEach
//    void setUp() {
//        this.cardVerificationCache = Caffeine.newBuilder()
//                .expireAfterWrite(1, TimeUnit.HOURS) // Adjust expiration time as needed
//                .build();
//        MockitoAnnotations.openMocks(this);
//    }
//
//    @Test
//    void getCardVerificationStats() {
//        // Mocking the repository
//        Page<CardVerification> mockPage = Mockito.mock(Page.class);
//        when(cardVerificationRepository.findAll(any(PageRequest.class))).thenReturn(mockPage);
//        when(mockPage.getTotalElements()).thenReturn(5L);
//
//        // Performing the test
//        CardStatsResponse result = cardVerificationService.getCardVerificationStats(1, 10);
//
//        // Verifying the result
//        assertEquals(5L, result.getSize());
//    }
//
//    @Test
//    void verifyCardFromCache() {
//        // Mocking cache and repository
//        when(cardVerificationCache.getIfPresent(anyString())).thenReturn(new CardVerification());
//        when(cardVerificationRepository.findByCardNumber(anyString())).thenReturn(new CardVerification());
//
//        // Performing the test
//        CardVerificationResponse result = cardVerificationService.verifyCard("1234567890123");
//
//        // Verifying the result
//        assertEquals(true, result.isSuccess());
//    }
//
//    @Test
//    void verifyCardFromDatabase() {
//        // Mocking cache and repository
//        when(cardVerificationCache.getIfPresent(anyString())).thenReturn(null);
//        when(cardVerificationRepository.findByCardNumber(anyString())).thenReturn(new CardVerification());
//
//        // Performing the test
//        CardVerificationResponse result = cardVerificationService.verifyCard("1234567890123");
//
//        // Verifying the result
//        assertEquals(true, result.isSuccess());
//    }
//
//    @Test
//    void verifyCardFromApi() {
//        // Mocking cache and repository
//        when(cardVerificationCache.getIfPresent(anyString())).thenReturn(null);
//        when(cardVerificationRepository.findByCardNumber(anyString())).thenReturn(null);
//
//        // Mocking WebClient response
//        BinResponse mockBinResponse = new BinResponse();
//        mockBinResponse.setScheme("scheme");
//        mockBinResponse.setType("type");
//        mockBinResponse.setBank(new BinResponse.Bank());
//        when(webClient.mutate().baseUrl(anyString())).thenReturn((WebClient.Builder) webClient);
//        when(webClient.build()).thenReturn(webClient);
//        when(webClient.get()).thenReturn(webClient);
//        when(webClient.uri(anyString())).thenReturn(webClient);
//        when(webClient.retrieve()).thenReturn(webClient);
//        when(webClient.toEntity(any())).thenReturn(Mono.just(ResponseEntity.ok(mockBinResponse)));
//
//        // Performing the test
//        CardVerificationResponse result = cardVerificationService.verifyCard("1234567890123");
//
//        // Verifying the result
//        assertEquals(true, result.isSuccess());
//    }
//
//    @Test
//    void verifyCardNotFoundInApi() {
//        // Mocking cache and repository
//        when(cardVerificationCache.getIfPresent(anyString())).thenReturn(null);
//        when(cardVerificationRepository.findByCardNumber(anyString())).thenReturn(null);
//
//// Mocking WebClient response for NotFoundException
//// Mocking WebClient response for NotFoundException
//        when(webClient.mutate()).thenReturn(webClient);
//        when(webClient.mutate().baseUrl(anyString())).thenReturn(webClient);
//        when(webClient.mutate().build()).thenReturn(webClient);
//        when(webClient.mutate().get()).thenReturn(requestHeadersUriSpecMock);
//        when(requestHeadersUriSpecMock.uri(anyString())).thenReturn(requestHeadersSpecMock);
//        when(requestHeadersSpecMock.retrieve()).thenReturn(webClient);
//        when(requestHeadersSpecMock.toEntity(any())).thenThrow(WebClientResponseException.NotFound.class);
//
//        // Performing the test
//        NotFoundException exception = org.junit.jupiter.api.Assertions.assertThrows(
//                NotFoundException.class,
//                () -> cardVerificationService.verifyCard("1234567890123")
//        );
//
//        // Verifying the exception message
//        assertEquals("Card details not found", exception.getMessage());
//    }
//}
