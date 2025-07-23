package com.zad.wallet.exception;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class InvalidCurrencyException extends RuntimeException {
    private final String currency;

}
