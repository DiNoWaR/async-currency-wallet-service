package com.zad.wallet.service;

import com.zad.wallet.repository.WalletRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Service
@Slf4j
public class PendingTxPollerService {

    private final WalletRepository walletRepository;
    private final ScheduledExecutorService scheduler;

    public PendingTxPollerService(WalletRepository walletRepository,
                                  @Qualifier("pendingTxScheduler") ScheduledExecutorService scheduler) {
        this.walletRepository = walletRepository;
        this.scheduler = scheduler;
        startPolling();
    }


    private void startPolling() {
        scheduler.scheduleWithFixedDelay(this::pollAndProcess, 0, 5, TimeUnit.SECONDS);
    }

    private void pollAndProcess() {
        try {
            log.info("Starting polling task...");
            walletRepository.processPendingTransactions();
        } catch (Exception ex) {
            log.error("Error processing pending transactions", ex);
        }
    }
}

