package com.zad.wallet.controller;

import com.zad.wallet.dto.ErrorResponse;
import com.zad.wallet.exception.InsufficientFundsException;
import com.zad.wallet.exception.InvalidCurrencyException;
import com.zad.wallet.exception.TxInProgressException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.server.ResponseStatusException;

@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleAll(Exception ex) {
        ErrorResponse error = new ErrorResponse(
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                "Internal Server Error",
                ex.getMessage()
        );
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
    }

    @ExceptionHandler(InsufficientFundsException.class)
    public ResponseEntity<ErrorResponse> handleInsufficientFunds(InsufficientFundsException ex) {
        ErrorResponse error = new ErrorResponse(
                HttpStatus.CONFLICT.value(),
                "Insufficient Funds",
                ex.getMessage()
        );
        return ResponseEntity.status(HttpStatus.CONFLICT).body(error);
    }

    @ExceptionHandler(TxInProgressException.class)
    public ResponseEntity<ErrorResponse> handleInProgress(TxInProgressException ex) {
        var resp = new ErrorResponse(HttpStatus.CONFLICT.value(), "Transaction submitted, idempotency key:", ex.getTrxKey());
        return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .body(resp);
    }

    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<ErrorResponse> handleRateLimit(ResponseStatusException exception) {
        if (exception.getStatusCode() == HttpStatus.TOO_MANY_REQUESTS) {
            ErrorResponse error = new ErrorResponse(
                    HttpStatus.TOO_MANY_REQUESTS.value(),
                    "Too many requests",
                    exception.getReason()
            );
            return ResponseEntity
                    .status(HttpStatus.TOO_MANY_REQUESTS)
                    .header("Retry-After", "60")
                    .body(error);
        }
        throw exception;
    }

    @ExceptionHandler(InvalidCurrencyException.class)
    public ResponseEntity<ErrorResponse> handleInvalidCurrency(InvalidCurrencyException ex) {
        var error = new ErrorResponse(HttpStatus.CONFLICT.value(), "Unsupported currency", ex.getCurrency());
        return ResponseEntity.status(HttpStatus.CONFLICT).body(error);
    }
}

