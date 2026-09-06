package com.incident.ingestion.service.impl;

import com.incident.ingestion.model.AlertEvent;
import com.incident.ingestion.service.AlertPublisher;
import com.incident.ingestion.constants.KafkaConstants;
import org.springframework.stereotype.Service;
import org.springframework.kafka.core.KafkaTemplate;

import java.util.concurrent.CompletableFuture;

@Service
public class AlertPublisherImpl implements AlertPublisher {

    private final KafkaTemplate<String, AlertEvent> kafkaTemplate;

    public AlertPublisherImpl(KafkaTemplate<String, AlertEvent> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    @Override
    public CompletableFuture<Void> publish(AlertEvent alertEvent) {
        return kafkaTemplate.send(KafkaConstants.ALERTS_TOPIC,
                        alertEvent.getAlertId().toString(), alertEvent)
                .thenApply(result -> null);
    }

}
