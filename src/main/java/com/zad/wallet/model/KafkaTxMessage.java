package com.zad.wallet.model;

import com.zad.wallet.dto.TxOperation;
import com.zad.wallet.dto.TxStatus;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Data
@Builder
public class KafkaTxMessage {
    @Builder.Default
    private final String trxId = UUID.randomUUID().toString();

    private String userId;
    private BigDecimal amount;
    private TxOperation operation;
    private TxStatus status;
    private Instant ts;
}
