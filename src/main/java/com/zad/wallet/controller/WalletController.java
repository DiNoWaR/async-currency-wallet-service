package com.zad.wallet.controller;

import com.zad.wallet.dto.*;
import com.zad.wallet.constants.Constants;
import com.zad.wallet.service.WalletService;
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


    @GetMapping("/status/{userId}")
    public ResponseEntity<TrxResponse> status(@PathVariable String userId) {
        var response = walletService.getLastTransaction(userId);
        return ResponseEntity.ok().body(response);
    }


    @GetMapping("/balance")
    public ResponseEntity<BalanceResponse> getUserBalance(@RequestAttribute("userId") String userId) {
        var balances = walletService.getUserBalances(userId);
        var response = new BalanceResponse(userId, balances);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/exchange")
    public ResponseEntity<BalanceResponse> exchange(@RequestAttribute("userId") String userId) {
        var balances = walletService.getUserBalances(userId);
        var response = new BalanceResponse(userId, balances);
        return ResponseEntity.ok(response);
    }
}
