package com.zad.wallet.service;

import com.zad.wallet.controller.WalletController;
import com.zad.wallet.dto.*;
import com.zad.wallet.service.config.TestWalletServiceConfig;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.Instant;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(WalletController.class)
@Import(TestWalletServiceConfig.class)
class WalletControllerTest {
    private static final String TOKEN = "mock-token";

    @BeforeEach
    void setup() {
        when(jwtService.validateJwtToken(TOKEN)).thenReturn(true);
        when(jwtService.getUserIdFromToken(TOKEN)).thenReturn("user1");
    }

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private WalletService walletService;

    @Autowired
    private JwtService jwtService;

    @Test
    void shouldReturn201OnDeposit() throws Exception {
        when(walletService.makeTransaction(
                eq("user1"), any(), any(), any(), eq(TxOperation.DEPOSIT), any()))
                .thenReturn("trx123");

        String requestJson = """
                    {
                        "amount": 100.00,
                        "currency": "usd"
                    }
                """;

        mockMvc.perform(post("/transactions/deposit")
                        .header("Authorization", "Bearer " + TOKEN)
                        .header("X-Idempotency-Key", "key123")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.operation").value("DEPOSIT"))
                .andExpect(jsonPath("$.currency").value("usd"));
    }

    @Test
    void shouldReturn201OnWithdraw() throws Exception {
        String requestJson = """
        {
            "amount": 50.00,
            "currency": "usd"
        }
    """;

        when(walletService.makeTransaction(
                eq("user1"), any(), any(), any(), eq(TxOperation.WITHDRAW), any()))
                .thenReturn("trx999");

        mockMvc.perform(post("/transactions/withdraw")
                        .header("Authorization", "Bearer " + TOKEN)
                        .header("X-Idempotency-Key", "withdraw-key")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.operation").value("WITHDRAW"))
                .andExpect(jsonPath("$.currency").value("usd"));
    }

    @Test
    void shouldReturn200OnStatus() throws Exception {
        TrxResponse mockResponse = new TrxResponse(
                "trx321", TxOperation.DEPOSIT, new BigDecimal("100.00"), TxStatus.PENDING, "usd", Instant.now()
        );

        when(walletService.getLastTransaction("user1"))
                .thenReturn(mockResponse);

        mockMvc.perform(get("/transactions/status/user1")
                        .header("Authorization", "Bearer " + TOKEN))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.trxId").value("trx321"))
                .andExpect(jsonPath("$.operation").value("DEPOSIT"));
    }
}


