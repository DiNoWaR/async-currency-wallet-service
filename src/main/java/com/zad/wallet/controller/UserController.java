package com.zad.wallet.controller;

import com.zad.wallet.dto.BalanceResponse;
import com.zad.wallet.dto.LogUserRequest;
import com.zad.wallet.dto.LoginUserResponse;
import com.zad.wallet.service.WalletService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
public class UserController {
    private final WalletService walletService;

    @PostMapping("")
    public ResponseEntity<LoginUserResponse> logUser(@RequestBody LogUserRequest request) {
        var response = new LoginUserResponse("", "", "");
        return ResponseEntity.ok(response);
    }

    @GetMapping("/balances/{userId}")
    public ResponseEntity<BalanceResponse> getUserBalance(@PathVariable String userId) {
        var balances = walletService.getUserBalances(userId);
        var response = new BalanceResponse(userId, balances);
        return ResponseEntity.ok(response);
    }
}

