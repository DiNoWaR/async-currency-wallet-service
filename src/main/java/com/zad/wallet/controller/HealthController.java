package com.zad.wallet.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/health")
public class HealthController {

    @Operation(
            summary = "Health check endpoint",
            tags = {"Health"}
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Service is up and running",
                    content = @Content),
    })
    @GetMapping("")
    public ResponseEntity<?> health() {
        return ResponseEntity.status(HttpStatus.OK).build();
    }
}
