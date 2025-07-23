package com.zad.wallet.dto;

import java.math.BigDecimal;

public record Balance(BigDecimal amount, String currency) {
}
