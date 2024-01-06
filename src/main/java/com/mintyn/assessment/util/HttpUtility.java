package com.mintyn.assessment.util;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.stereotype.Component;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

@Log4j2
@Component
@RequiredArgsConstructor
public class HttpUtility {

    @Autowired
    final RestTemplate restTemplate;

    public  <T> ResponseEntity<T> makeRestCall(String url, HttpMethod method, HttpHeaders headers,Object requestBody, Class<T> responseType, MultiValueMap<String, String> queryParams) {
        try {
            UriComponentsBuilder uriBuilder = UriComponentsBuilder.fromHttpUrl(url).queryParams(queryParams);
            HttpEntity<Object> requestEntity = new HttpEntity<>(requestBody, headers);
String a=uriBuilder.toUriString();
            ResponseEntity<T> responseEntity = restTemplate.exchange(uriBuilder.toUriString(), method, requestEntity, responseType);
            return responseEntity;
        } catch (HttpServerErrorException ex) {
            HttpStatus statusCode = (HttpStatus) ex.getStatusCode();
            HttpHeaders responseHeaders = ex.getResponseHeaders();
            T responseBody = (T) ex.getResponseBodyAsString();

            // Handle the 500 error response
            return new ResponseEntity<>(responseBody, responseHeaders, statusCode);
        } catch (HttpMessageNotReadableException ex) {
            // Handle the JSON parse error
            log.info("Handle the JSON parse error");
            HttpStatus statusCode = HttpStatus.BAD_REQUEST; // Default to 400 error
            HttpHeaders responseHeaders = null; // No response headers available
            T responseBody = null; // No response body for JSON parse error
            return new ResponseEntity<>(responseBody, responseHeaders, statusCode);
        }
        catch (IllegalArgumentException ex)
        {
            log.info("IllegalArgument Exception");
            HttpStatus statusCode = HttpStatus.BAD_REQUEST;
            HttpHeaders responseHeaders = null;
            T responseBody = (T)ex.getMessage();
            return new ResponseEntity<>(responseBody, responseHeaders, statusCode);
        }

        catch (RestClientException ex) {
            // Handle other RestClient-related exceptions
            log.info("RestClient-related exceptions occured");
            HttpStatus statusCode = HttpStatus.INTERNAL_SERVER_ERROR; // Default to 500 error
            HttpHeaders responseHeaders = null; // No response headers available
            T responseBody = (T)ex.getMessage(); // No response body for other exceptions
            return new ResponseEntity<>(responseBody, responseHeaders, statusCode);
        }

    }
}
