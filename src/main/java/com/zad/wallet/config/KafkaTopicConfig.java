package com.zad.wallet.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.common.config.TopicConfig;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
public class KafkaTopicConfig {

    @Value("${kafka.topic.transactions}")
    private String trxTopic;

    @Value("${kafka.topic.transactions.partitions")
    private int topicPartitions;


    @Bean
    public NewTopic txTopic() {
        return TopicBuilder.name(trxTopic)
                .partitions(topicPartitions)
                .replicas(1)
                .config(TopicConfig.CLEANUP_POLICY_CONFIG, TopicConfig.CLEANUP_POLICY_DELETE)
                .build();
    }
}
