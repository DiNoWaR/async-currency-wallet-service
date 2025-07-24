package com.zad.wallet.controller;

import com.zad.wallet.dto.*;
import com.zad.wallet.constants.Constants;
import com.zad.wallet.service.WalletService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;

@RestController
@RequestMapping("/transactions")
@RequiredArgsConstructor
public class WalletController {
    private final WalletService walletService;

    @Operation(summary = "Deposit funds into user wallet",
            tags = {"Transactions" },
            security = @SecurityRequirement(name = "bearerAuth"))
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Deposit transaction created",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = TrxResponse.class))),
            @ApiResponse(responseCode = "400", description = "Invalid request payload",
                    content = @Content),
            @ApiResponse(responseCode = "401", description = "Unauthorized - missing or invalid Authorization header",
                    content = @Content),
            @ApiResponse(responseCode = "500", description = "Internal server error",
                    content = @Content),
            @ApiResponse(responseCode = "409", description = "Idempotency conflict – transaction already exists",
                    content = @Content)
    })
    @Parameters(value = {
            @Parameter(name = "Authorization", description = "Bearer token", required = true,
                    in = ParameterIn.HEADER, example = "Bearer eyJhbGciOi...")
    })
    @PostMapping("/deposit")
    public ResponseEntity<?> deposit(
            @RequestAttribute("userId") String userId,
            @RequestHeader(value = Constants.IDEMPOTENCY_KEY_HEADER, required = false, defaultValue = "") String trxKey,
            @Valid @RequestBody TxRequest request) {

        var now = Instant.now();
        var trxId = walletService.makeTransaction(trxKey, userId, request.getAmount(), request.getCurrency(), TxOperation.DEPOSIT, now);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(new TrxResponse(trxId, TxOperation.DEPOSIT, request.getAmount(), TxStatus.PENDING, request.getCurrency(), now));

    }


    @Operation(summary = "Withdraw funds from user's wallet",
            tags = {"Transactions" },
            security = {
                    @SecurityRequirement(name = "bearerAuth")
            })
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Withdrawal initiated successfully",
                    content = @Content(schema = @Schema(implementation = TrxResponse.class))),
            @ApiResponse(responseCode = "400", description = "Invalid request parameters",
                    content = @Content),
            @ApiResponse(responseCode = "401", description = "Unauthorized – missing or invalid Authorization header",
                    content = @Content),
            @ApiResponse(responseCode = "409", description = "Idempotency conflict – transaction already exists",
                    content = @Content),
            @ApiResponse(responseCode = "500", description = "Internal server error",
                    content = @Content),
    })
    @Parameters(value = {
            @Parameter(name = "Authorization", description = "Bearer token", required = true,
                    in = ParameterIn.HEADER, example = "Bearer eyJhbGciOi...")
    })
    @PostMapping("/withdraw")
    public ResponseEntity<?> withdraw(
            @RequestAttribute("userId") String userId,
            @RequestHeader(value = Constants.IDEMPOTENCY_KEY_HEADER, required = false, defaultValue = "") String trxKey,
            @Valid @RequestBody TxRequest request) {

        var now = Instant.now();
        var trxId = walletService.makeTransaction(trxKey, userId, request.getAmount(), request.getCurrency(), TxOperation.WITHDRAW, now);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(new TrxResponse(trxId, TxOperation.WITHDRAW, request.getAmount(), TxStatus.PENDING, request.getCurrency(), now));
    }


    @Operation(
            summary = "Get last transaction status for user",
            tags = {"Transactions" }
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved last transaction status",
                    content = @Content(schema = @Schema(implementation = TrxResponse.class))),
            @ApiResponse(responseCode = "500", description = "Internal server error",
                    content = @Content),
    })
    @Parameters(value = {
            @Parameter(name = "userId", description = "ID of the user", required = true,
                    in = ParameterIn.PATH, example = "12345")
    })
    @GetMapping("/status/{userId}")
    public ResponseEntity<TrxResponse> status(@PathVariable String userId) {
        var response = walletService.getLastTransaction(userId);
        return ResponseEntity.ok().body(response);
    }


    @Operation(
            summary = "Get user wallet balance",
            tags = {"Balances"}
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved user balances",
                    content = @Content(schema = @Schema(implementation = BalanceResponse.class))),
            @ApiResponse(responseCode = "404", description = "User not found",
                    content = @Content),
            @ApiResponse(responseCode = "500", description = "Internal server error",
                    content = @Content),
    })
    @Parameters(value = {
            @Parameter(name = "userId", description = "ID of the user", required = true,
                    in = ParameterIn.PATH, example = "12345")
    })
    @GetMapping("/balance/{userId}")
    public ResponseEntity<BalanceResponse> getUserBalance(@PathVariable String userId) {
        var balances = walletService.getUserBalances(userId);
        var response = new BalanceResponse(userId, balances);
        return ResponseEntity.ok(response);
    }

    @Operation(
            summary = "Get exchange rate between two currencies",
            tags = {"Exchange Rates"}
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Exchange rate retrieved successfully",
                    content = @Content(schema = @Schema(implementation = ExchangeRateResponse.class))),
            @ApiResponse(responseCode = "400", description = "Invalid request parameters",
                    content = @Content),
            @ApiResponse(responseCode = "404", description = "Exchange rate not found",
                    content = @Content),
            @ApiResponse(responseCode = "500", description = "Internal server error",
                    content = @Content),
    })
    @GetMapping("/exchange")
    public ResponseEntity<ExchangeRateResponse> getExchangeRates(@Valid @RequestBody ExchangeRateRequest request) {
        var rate = walletService.getExchangeRates(request.getCurrencyFrom().toLowerCase(), request.getCurrencyTo().toLowerCase());
        var response = new ExchangeRateResponse(
                request.getCurrencyFrom().toUpperCase(),
                request.getCurrencyTo().toUpperCase(),
                rate
        );
        return ResponseEntity.ok(response);
    }
}
