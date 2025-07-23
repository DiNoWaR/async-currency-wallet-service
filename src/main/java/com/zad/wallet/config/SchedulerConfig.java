package com.zad.wallet.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

@Configuration
public class SchedulerConfig {

    @Bean(name = "pendingTxScheduler")
    public ScheduledExecutorService pendingTxScheduler() {
        var threads = Runtime.getRuntime().availableProcessors();
        return Executors.newScheduledThreadPool(threads, runnable -> {
            var thread = new Thread(runnable, "pending-tx-worker");
            thread.setDaemon(true);
            return thread;
        });
    }
}

