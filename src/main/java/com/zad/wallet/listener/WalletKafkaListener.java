package com.zad.wallet.listener;

import com.zad.wallet.model.KafkaTxMessage;
import com.zad.wallet.repository.WalletRepository;
import lombok.RequiredArgsConstructor;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@EnableKafka
@Component
@RequiredArgsConstructor
public class WalletKafkaListener {

    private final WalletRepository walletRepository;

    @KafkaListener(
            topics = "${kafka.topic.transactions}",
            groupId = "wallet-service-group"
    )
    public void consume(KafkaTxMessage msg, ConsumerRecord<String, KafkaTxMessage> record) {
        walletRepository.persistTransaction(
                msg.getTrxId(),
                msg.getUserId(),
                msg.getOperation().name(),
                msg.getCurrency().toLowerCase(),
                msg.getAmount(),
                msg.getStatus().name(),
                msg.getTs()
        );
    }
}
