package com.zad.wallet.dto;

import lombok.AllArgsConstructor;
import org.checkerframework.checker.units.qual.A;

import java.util.List;

@AllArgsConstructor
public class BalanceResponse {
    String userId;
    List<Balance> balances;
}

