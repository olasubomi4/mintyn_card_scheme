package com.mintyn.assessment.services;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.google.gson.GsonBuilder;
import com.mintyn.assessment.configs.AppConfigurations;
import com.mintyn.assessment.dto.bin.response.BankInfo;
import com.mintyn.assessment.dto.bin.response.BinResponse;
import com.mintyn.assessment.dto.cardverification.response.CardStatsResponse;
import com.mintyn.assessment.dto.cardverification.response.CardVerificationPayload;
import com.mintyn.assessment.dto.cardverification.response.CardVerificationResponse;
import com.mintyn.assessment.entities.CardVerification;
import com.mintyn.assessment.enums.ResponseMessages;
import com.mintyn.assessment.exceptions.CardServiceVerificationException;
import com.mintyn.assessment.repositories.CardVerificationRepository;
import okhttp3.mockwebserver.SocketPolicy;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mockito.*;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import static org.mockito.ArgumentMatchers.anyString;

import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.reactive.function.client.WebClient;
import org.webjars.NotFoundException;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@RunWith(SpringRunner.class)
@TestPropertySource(locations = "classpath:application.properties")
class CardVerificationServiceImplTest {

    @Spy
    private AppConfigurations appConfigurations;

    @Mock
    private Cache<String, Object> cardVerificationCache;

    @Mock
    private CardVerificationRepository cardVerificationRepository;

    @InjectMocks
    private CardVerificationServiceImpl cardVerificationService;
    private MockWebServer mockWebServer;


    @BeforeEach
    void setUp() throws IOException {
        this.cardVerificationCache = Caffeine.newBuilder()
                .expireAfterWrite(1, TimeUnit.HOURS)
                .build();

        MockitoAnnotations.openMocks(this);

        mockWebServer = new MockWebServer();
        mockWebServer.start();

    }
    @AfterEach
    public void tearDown() throws Exception {
        // Shutdown the MockWebServer
        mockWebServer.shutdown();
    }

    @Test
    void getCardVerificationStats() {
        // Mocking the repository
        Page<CardVerification> mockPage = mock(Page.class);
        when(cardVerificationRepository.findAll(any(PageRequest.class))).thenReturn(mockPage);
        when(mockPage.getTotalElements()).thenReturn(5L);

        // Performing the test
        CardStatsResponse result = cardVerificationService.getCardVerificationStats(1, 10);

        // Verifying the result
        assertEquals(5L, result.getSize());
    }

    @Test
    void verifyCardFromCache() {
        //Given
        String sampleValidCard="4571731213424242532";

        // Mocking cache and repository
        when(cardVerificationCache.getIfPresent(anyString())).thenReturn(new CardVerification());
        when(cardVerificationRepository.findByCardNumber(anyString())).thenReturn(new CardVerification());

        // Performing the test
        CardVerificationResponse result = cardVerificationService.verifyCard(sampleValidCard);

        // Verifying the result
        assertEquals(true, result.isSuccess());
    }

    @Test
    void verifyCardFromDatabase() {
        //Given
        String sampleValidCard="4571731213424242532";

        // Mocking cache and repository
        when(cardVerificationCache.getIfPresent(anyString())).thenReturn(null);
        when(cardVerificationRepository.findByCardNumber(anyString())).thenReturn(new CardVerification());

        // Performing the test
        CardVerificationResponse result = cardVerificationService.verifyCard(sampleValidCard);

        // Verifying the result
        assertEquals(true, result.isSuccess());
    }
    @Test
    public void testGetBinDetails_Success() {
        //Given
        String sampleValidCard="4571731213424242532";
        BinResponse binResponse= BinResponse.builder().scheme("visa").type("debit").bank(BankInfo.builder()
                .name("Jyske Bank A/S").build()).build();

        CardVerificationResponse expectedCardVerificationResponse= CardVerificationResponse.builder().success(true)
                .payload(CardVerificationPayload.builder().scheme("visa").type("debit").bank("Jyske Bank A/S").build())
                .build();

        // Enqueue a successful response from MockWebServer
        mockWebServer.enqueue(new MockResponse()
                .setResponseCode(200)
                .setHeader("Content-Type", "application/json")
                .setBody(convertObjectToString(binResponse)));

        // Set the base URL of the MockWebServer in your WebClient
        String mockWebServerBaseUrl = mockWebServer.url("/").toString();
        WebClient webClient = WebClient.builder().baseUrl(mockWebServerBaseUrl).build();

        // Create an instance of your class and set the WebClient

        cardVerificationService  = new CardVerificationServiceImpl(appConfigurations,webClient
                ,cardVerificationRepository,cardVerificationCache);

        // Perform the method invocation
        CardVerificationResponse resultBinResponse = cardVerificationService.verifyCard(sampleValidCard);

        assertEquals(expectedCardVerificationResponse,resultBinResponse);
    }

    @Test
    void verifyCardNotFoundInApi() {
        //Given
        String sampleInvalidCard="4571731213424242532";
        BinResponse binResponse= new BinResponse();
        binResponse.setNumber(null);

        // Enqueue a successful response from MockWebServer
        mockWebServer.enqueue(new MockResponse()
                .setResponseCode(200)
                .setHeader("Content-Type", "application/json")
                .setBody(convertObjectToString(binResponse)));

        // Set the base URL of the MockWebServer in your WebClient
        String mockWebServerBaseUrl = mockWebServer.url("/").toString();
        WebClient webClient = WebClient.builder().baseUrl(mockWebServerBaseUrl).build();

        // Create an instance of your class and set the WebClient
        cardVerificationService  = new CardVerificationServiceImpl(appConfigurations,webClient
                ,cardVerificationRepository,cardVerificationCache);

        // Performing the test
        NotFoundException exception = org.junit.jupiter.api.Assertions.assertThrows(
                NotFoundException.class,
                () -> cardVerificationService.verifyCard(sampleInvalidCard)
        );

        // Verifying the exception message
        assertEquals(ResponseMessages.CARD_DETAILS_NOT_FOUND.getResponseMessage(), exception.getMessage());
    }

    @Test
    void testGetBinDetails_Timeout() {
        //Given
        String sampleInvalidCard="4571731213424242532";

        // Enqueue a successful response from MockWebServer
        mockWebServer.enqueue(new MockResponse()
                .throttleBody(1024, 3, TimeUnit.SECONDS) // 3 bytes/sec, 10 seconds delay
                .setSocketPolicy(SocketPolicy.NO_RESPONSE));

        // Set the base URL of the MockWebServer in your WebClient
        String mockWebServerBaseUrl = mockWebServer.url("/").toString();
        WebClient webClient = WebClient.builder().baseUrl(mockWebServerBaseUrl).build();

        // Create an instance of your class and set the WebClient
        cardVerificationService  = new CardVerificationServiceImpl(appConfigurations,webClient
                ,cardVerificationRepository,cardVerificationCache);

        // Performing the test
        CardServiceVerificationException exception = org.junit.jupiter.api.Assertions.assertThrows(
                CardServiceVerificationException.class,
                () -> cardVerificationService.verifyCard(sampleInvalidCard)
        );
        // Verifying the exception message
        assertEquals(ResponseMessages.SERVICE_IS_UNAVAILABLE.getResponseMessage(), exception.getMessage());
    }

    public String convertObjectToString(Object object)
    {
        return new GsonBuilder().create().toJson(object);
    }
}
