package com.zad.wallet.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ExchangeRateRequest {
    @NotNull
    @JsonProperty("currency_from")
    private String currencyFrom;

    @NotNull
    @JsonProperty("currency_to")
    private String currencyTo;
}
