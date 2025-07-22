package com.zad.wallet.dto;

public record ErrorResponse(int status, String message, String error) {
}

