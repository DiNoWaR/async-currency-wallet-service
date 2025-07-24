package com.zad.wallet.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class InvalidCurrencyException extends RuntimeException {
    private final String currency;

}
