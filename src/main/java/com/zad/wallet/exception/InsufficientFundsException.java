package com.zad.wallet.exception;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@AllArgsConstructor
public class InsufficientFundsException extends RuntimeException {
    private final String trxId;
    private final String userId;
}
