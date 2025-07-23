package com.zad.wallet.controller;

import com.zad.wallet.dto.TrxResponse;
import com.zad.wallet.dto.TxOperation;
import com.zad.wallet.dto.TxRequest;
import com.zad.wallet.dto.TxStatus;
import com.zad.wallet.constants.Constants;
import com.zad.wallet.service.WalletService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.List;

@RestController
@RequestMapping("/")
@RequiredArgsConstructor
public class WalletController {
    private final WalletService walletService;

    @PostMapping("/transactions/deposit")
    public ResponseEntity<?> deposit(
            @RequestHeader(value = Constants.IDEMPOTENCY_KEY_HEADER, required = false) String trxKey,
            @Valid @RequestBody TxRequest request) {

        var now = Instant.now();
        var trxId = walletService.makeTransaction(trxKey, request.getUserId(), request.getAmount(), request.getCurrency(), TxOperation.DEPOSIT, now);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(new TrxResponse(trxId, TxOperation.DEPOSIT, request.getAmount(), TxStatus.PENDING, now));

    }


    @PostMapping("/transactions/withdraw")
    public ResponseEntity<?> withdraw(
            @RequestHeader(value = Constants.IDEMPOTENCY_KEY_HEADER, required = false) String trxKey,
            @Valid @RequestBody TxRequest request) {

        var now = Instant.now();
        var trxId = walletService.makeTransaction(trxKey, request.getUserId(), request.getAmount(), request.getCurrency(), TxOperation.WITHDRAW, now);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(new TrxResponse(trxId, TxOperation.WITHDRAW, request.getAmount(), TxStatus.PENDING, now));

    }


    @GetMapping("/transactions/{trxId}")
    public List<TxStatus> status(@PathVariable String trxId) {
        return null;
    }
}
