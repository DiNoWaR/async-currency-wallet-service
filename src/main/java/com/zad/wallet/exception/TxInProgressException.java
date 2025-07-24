package com.zad.wallet.exception;

import com.zad.wallet.dto.TxOperation;
import com.zad.wallet.dto.TxStatus;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;
import java.time.Instant;

@Data
@AllArgsConstructor
public class TxInProgressException extends RuntimeException {
    private final String trxKey;
    private final TxOperation operation;
    private final BigDecimal amount;
    private final TxStatus status;
    private final String currency;
    private final Instant ts;

}
