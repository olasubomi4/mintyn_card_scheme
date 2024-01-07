package com.mintyn.assessment.service;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.mintyn.assessment.configs.AppConfigurations;
import com.mintyn.assessment.dto.bin.response.BinResponse;
import com.mintyn.assessment.dto.cardverification.response.CardStatsResponse;
import com.mintyn.assessment.dto.cardverification.response.CardVerificationPayload;
import com.mintyn.assessment.dto.cardverification.response.CardVerificationResponse;
import com.mintyn.assessment.entities.CardVerification;
import com.mintyn.assessment.repositories.CardVerificationRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.runner.RunWith;
import org.mockito.*;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import okhttp3.mockwebserver.RecordedRequest;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import static org.mockito.ArgumentMatchers.anyString;

import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.reactive.function.client.WebClient;
import org.webjars.NotFoundException;
import reactor.core.publisher.Mono;

import java.io.IOException;
import java.time.Duration;
import java.time.temporal.ChronoUnit;
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
    private Cache<String, CardVerification> cardVerificationCache;

    @Mock
    private CardVerificationRepository cardVerificationRepository;

    @InjectMocks
    private CardVerificationServiceImpl cardVerificationService;
    private MockWebServer mockWebServer;


    @BeforeEach
    void setUp() throws IOException {
        this.cardVerificationCache = Caffeine.newBuilder()
                .expireAfterWrite(1, TimeUnit.HOURS) // Adjust expiration time as needed
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
        // Mocking cache and repository
        when(cardVerificationCache.getIfPresent(anyString())).thenReturn(new CardVerification());
        when(cardVerificationRepository.findByCardNumber(anyString())).thenReturn(new CardVerification());
        when(appConfigurations.getBinDetailApiUrl()).thenReturn( System.getenv("BIN_DETAIL_API_URL"));

        // Performing the test
        CardVerificationResponse result = cardVerificationService.verifyCard("1234567890123");

        // Verifying the result
        assertEquals(true, result.isSuccess());
    }

    @Test
    void verifyCardFromDatabase() {
        // Mocking cache and repository
        when(cardVerificationCache.getIfPresent(anyString())).thenReturn(null);
        when(cardVerificationRepository.findByCardNumber(anyString())).thenReturn(new CardVerification());

        // Performing the test
        CardVerificationResponse result = cardVerificationService.verifyCard("1234567890123");

        // Verifying the result
        assertEquals(true, result.isSuccess());
    }
    @Test
    public void testGetBinDetails_Success() {
               String sampleValidCard="4571731213424242532";
        CardVerificationResponse expectedCardVerificationResponse= CardVerificationResponse.builder().success(true)
                .payload(CardVerificationPayload.builder().scheme("visa").type("debit").bank("Jyske Bank A/S").build()).build();

        // Enqueue a successful response from MockWebServer

        BinResponse binResponse2= new BinResponse();
        binResponse2.setScheme("asf");
        binResponse2.setType("test");
        mockWebServer.enqueue(new MockResponse()
                .setResponseCode(200)
                .setHeader("Content-Type", "application/json")
                .setBody("{\n" +
                        "    \"number\": {},\n" +
                        "    \"scheme\": \"visa\",\n" +
                        "    \"type\": \"debit\",\n" +
                        "    \"brand\": \"Visa Classic\",\n" +
                        "    \"country\": {\n" +
                        "        \"numeric\": \"208\",\n" +
                        "        \"alpha2\": \"DK\",\n" +
                        "        \"name\": \"Denmark\",\n" +
                        "        \"emoji\": \"\uD83C\uDDE9\uD83C\uDDF0\",\n" +
                        "        \"currency\": \"DKK\",\n" +
                        "        \"latitude\": 56,\n" +
                        "        \"longitude\": 10\n" +
                        "    },\n" +
                        "    \"bank\": {\n" +
                        "        \"name\": \"Jyske Bank A/S\"\n" +
                        "    }\n" +
                        "}"));

        // Set the base URL of the MockWebServer in your WebClient
        String mockWebServerBaseUrl = mockWebServer.url("/").toString();
        WebClient webClient = WebClient.builder().baseUrl(mockWebServerBaseUrl).build();

        // Create an instance of your class and set the WebClient

        cardVerificationService  = new CardVerificationServiceImpl(appConfigurations,webClient,cardVerificationRepository,cardVerificationCache);

        // Perform the method invocation
        CardVerificationResponse binResponse = cardVerificationService.verifyCard(sampleValidCard);

        assertEquals(expectedCardVerificationResponse,binResponse);
    }

    @Test
    void verifyCardNotFoundInApi() {
        String sampleInvalidCard="4571731213424242532";
        // Enqueue a successful response from MockWebServer

        BinResponse binResponse2= new BinResponse();
        binResponse2.setScheme("asf");
        binResponse2.setType("test");
        mockWebServer.enqueue(new MockResponse()
                .setResponseCode(200)
                .setHeader("Content-Type", "application/json")
                .setBody("{\n" +
                        "    \"number\": null,\n" +
                        "    \"country\": {},\n" +
                        "    \"bank\": {}\n" +
                        "}"));

        // Set the base URL of the MockWebServer in your WebClient
        String mockWebServerBaseUrl = mockWebServer.url("/").toString();
        WebClient webClient = WebClient.builder().baseUrl(mockWebServerBaseUrl).build();

        // Create an instance of your class and set the WebClient
        cardVerificationService  = new CardVerificationServiceImpl(appConfigurations,webClient,cardVerificationRepository,cardVerificationCache);

        // Performing the test
        NotFoundException exception = org.junit.jupiter.api.Assertions.assertThrows(
                NotFoundException.class,
                () -> cardVerificationService.verifyCard(sampleInvalidCard)
        );
        // Verifying the exception message
        assertEquals("Card details not found", exception.getMessage());
    }
}
