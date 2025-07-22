package com.zad.wallet.exception;


import lombok.Getter;

public class TxInProgressException extends RuntimeException {
    @Getter
    private final String trxId;

    public TxInProgressException(String trxId) {
        this.trxId = trxId;
    }
}

