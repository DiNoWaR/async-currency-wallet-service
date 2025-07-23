package com.zad.wallet.exception;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class InsufficientFundsException extends RuntimeException {
    private final String trxId;
    private final String userId;
}
