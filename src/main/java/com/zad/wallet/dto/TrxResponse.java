package com.zad.wallet.dto;

import java.math.BigDecimal;
import java.time.Instant;

public record TrxResponse(
        String trxId,
        TxOperation operation,
        BigDecimal amount,
        TxStatus status,
        String currency,
        Instant ts) {
}