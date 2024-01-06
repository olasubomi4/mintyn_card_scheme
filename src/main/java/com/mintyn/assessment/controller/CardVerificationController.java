package com.mintyn.assessment.controller;

import com.mintyn.assessment.dto.cardverification.response.CardStatsResponse;
import com.mintyn.assessment.dto.cardverification.response.CardVerificationResponse;
import com.mintyn.assessment.exception.ErrorResponse;
import com.mintyn.assessment.service.CardVerificationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Pattern;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;


@RestController
@Validated
@RequestMapping("/api/v1/card-scheme")
public class CardVerificationController {
    @Autowired
    private CardVerificationService cardVerificationService;


    @Operation(summary = "Get Card Verification Stats",
            description = "Retrieve statistics about card verifications based on start and limit parameters.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved card verification stats",
                    content = @Content(mediaType = "application/json",array = @ArraySchema(schema = @Schema(implementation = CardStatsResponse.class)))),
            @ApiResponse(responseCode = "500", description = "Internal Server Error",content = @Content(array = @ArraySchema(schema = @Schema(implementation = ErrorResponse.class))))})
    @GetMapping("/stats")
    public ResponseEntity<CardStatsResponse> getCardVerificationStats(
            @RequestParam @Min(value = 0,message = "Start must be greater than or equals to zero") int start,
            @RequestParam @Min(value=0,message = "limit must be greater than or equals to zero") int limit
    ) {
        CardStatsResponse stats = cardVerificationService.getCardVerificationStats(start, limit);
        return ResponseEntity.ok(stats);
    }

    @Operation(summary = "Verify Card",
            description = "Verify card details based on the provided card number.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully verified card details",
                    content = @Content(mediaType = "application/json",array =
                    @ArraySchema(schema = @Schema(implementation = CardVerificationResponse.class)))),
            @ApiResponse(responseCode = "400", description = "Bad Request: Invalid card number format",
                    content = @Content(mediaType = "application/json",array =
                    @ArraySchema(schema = @Schema(implementation = ErrorResponse.class)))),
            @ApiResponse(responseCode = "404", description = "Not Found: Card details not found",
                    content = @Content(mediaType = "application/json",array =
                    @ArraySchema(schema = @Schema(implementation = ErrorResponse.class)))),
            @ApiResponse(responseCode = "500", description = "Internal Server Error",content = @Content(array = @ArraySchema(schema = @Schema(implementation = ErrorResponse.class))))})
    @GetMapping("/verify/{cardNumber}")
    public ResponseEntity<CardVerificationResponse> verifyCard(@PathVariable @Pattern(regexp = "^[0-9]{13,19}$", message = "Invalid card number format. Please enter a numeric string with a length between 13 and 19 characters.")
                                                                   String cardNumber) {
        CardVerificationResponse cardVerificationResponse=cardVerificationService.verifyCard(cardNumber);
        return ResponseEntity.ok(cardVerificationResponse);
    }
}
