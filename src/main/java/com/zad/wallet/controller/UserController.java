package com.zad.wallet.controller;

import com.zad.wallet.dto.BalanceResponse;
import com.zad.wallet.dto.LogUserRequest;
import com.zad.wallet.dto.LoginUserResponse;
import com.zad.wallet.service.WalletService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {
    private final WalletService walletService;

    @Operation(
            summary = "Authenticate user by username",
            tags = {"Authentication" }
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User logged in successfully",
                    content = @Content(schema = @Schema(implementation = LoginUserResponse.class))),
            @ApiResponse(responseCode = "400", description = "Invalid request payload", content = @Content),
            @ApiResponse(responseCode = "500", description = "Internal server error",
                    content = @Content),
    })
    @io.swagger.v3.oas.annotations.parameters.RequestBody(
            description = "User login request",
            required = true,
            content = @Content(schema = @Schema(implementation = LogUserRequest.class))
    )
    @PostMapping("")
    public ResponseEntity<LoginUserResponse> logUser(@RequestBody LogUserRequest request) {
        var response = walletService.logUser(request.getUsername());
        return ResponseEntity.ok(response);
    }
}

