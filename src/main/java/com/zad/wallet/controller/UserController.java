package com.zad.wallet.controller;

import com.zad.wallet.dto.BalanceResponse;
import com.zad.wallet.dto.LogUserRequest;
import com.zad.wallet.dto.LoginUserResponse;
import com.zad.wallet.service.WalletService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {
    private final WalletService walletService;

    @PostMapping("")
    public ResponseEntity<LoginUserResponse> logUser(@RequestBody LogUserRequest request) {
        var response = walletService.logUser(request.getUsername());
        return ResponseEntity.ok(response);
    }
}

