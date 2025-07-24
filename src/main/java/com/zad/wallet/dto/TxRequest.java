package com.zad.wallet.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@EqualsAndHashCode(exclude = "amount")
public class TxRequest {

    @NotNull
    @DecimalMin(value = "0.000001", inclusive = true, message = "Amount must be positive")
    @JsonProperty("amount")
    private BigDecimal amount;

    @NotNull
    @JsonProperty("currency")
    private String currency;
}
