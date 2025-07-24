package com.zad.wallet.dto;

import lombok.AllArgsConstructor;

import java.util.List;

public record BalanceResponse(String userId, List<Balance> balances) { }

