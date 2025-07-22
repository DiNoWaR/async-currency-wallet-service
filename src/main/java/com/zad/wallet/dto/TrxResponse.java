package com.zad.wallet.dto;

import com.zad.wallet.dto.TxOperation;
import com.zad.wallet.dto.TxStatus;

import java.math.BigDecimal;
import java.time.Instant;

public record TrxResponse(
        String trxId,
        TxOperation operation,
        BigDecimal amount,
        TxStatus status,
        Instant ts
) {
}