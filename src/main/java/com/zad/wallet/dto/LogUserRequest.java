package com.zad.wallet.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class LogUserRequest {
    @NotNull
    String username;

    @NotNull
    String password;
}
