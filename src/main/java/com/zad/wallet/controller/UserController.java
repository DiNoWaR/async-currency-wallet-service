package com.zad.wallet.controller;

import com.zad.wallet.dto.Balance;
import com.zad.wallet.dto.BalanceResponse;
import com.zad.wallet.service.WalletService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
public class UserController {
    private final WalletService walletService;

    @GetMapping("/balances/{userId}")
    public  ResponseEntity<BalanceResponse> getUserBalance(@PathVariable String userId) {
        List<Balance> balances = walletService.getUserBalances(userId);
        BalanceResponse response = new BalanceResponse(userId, balances);
        return ResponseEntity.ok(response);
    }
}

