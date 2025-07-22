package com.zad.wallet.controller;

import com.zad.wallet.dto.TrxResponse;
import com.zad.wallet.dto.TxOperation;
import com.zad.wallet.dto.TxRequest;
import com.zad.wallet.dto.TxStatus;
import com.zad.wallet.service.RateLimiterService;
import com.zad.wallet.constants.Constants;
import com.zad.wallet.service.WalletService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

@RestController
@RequestMapping("/")
@RequiredArgsConstructor
public class WalletController {

    private final RateLimiterService rateLimiter;
    private final WalletService walletService;

    @Value("${kafka.topic.transactions}")
    private int trxTopic;


    @PostMapping("/transactions/deposit")
    public ResponseEntity<?> deposit(
            @RequestHeader(value = Constants.IDEMPOTENCY_KEY_HEADER, required = false) String trxKey,
            @Valid @RequestBody TxRequest request,
            HttpServletRequest httpRequest) {

        var clientIp = extractClientIp(httpRequest);
        rateLimiter.check(clientIp);

        var now = Instant.now();
        var trxId = walletService.makeTransaction(trxKey, request.getUserId(), request.getAmount(), TxOperation.DEPOSIT, now);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(new TrxResponse(trxId, TxOperation.DEPOSIT, request.getAmount(), TxStatus.PENDING, now));

    }


    @PostMapping("/transactions/withdraw")
    public ResponseEntity<?> withdraw(
            @RequestHeader(value = Constants.IDEMPOTENCY_KEY_HEADER, required = false) String trxKey,
            @Valid @RequestBody TxRequest request,
            HttpServletRequest httpRequest) {

        var clientIp = extractClientIp(httpRequest);
        rateLimiter.check(clientIp);

        var now = Instant.now();
        var trxId = walletService.makeTransaction(trxKey, request.getUserId(), request.getAmount(), TxOperation.WITHDRAW, now);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(new TrxResponse(trxId, TxOperation.WITHDRAW, request.getAmount(), TxStatus.PENDING, now));

    }


    @GetMapping("/balance/{userId}")
    public BigDecimal getBalance(@PathVariable Long userId) {
        return null;
    }

    @GetMapping("/status/{trxId}")
    public List<TxStatus> status(@PathVariable String trxId) {
        return null;
    }

    private String extractClientIp(HttpServletRequest request) {
        var xff = request.getHeader("X-Forwarded-For");
        if (xff != null && !xff.isBlank()) {
            return xff.split(",")[0].trim();
        }
        var xrip = request.getHeader("X-Real-IP");
        if (xrip != null && !xrip.isBlank()) {
            return xrip;
        }
        return request.getRemoteAddr();
    }
}
