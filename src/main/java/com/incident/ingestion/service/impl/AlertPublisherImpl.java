package com.incident.ingestion.service.impl;

import com.incident.ingestion.model.AlertRequest;
import com.incident.ingestion.service.AlertPublisher;
import org.springframework.kafka.core.KafkaTemplate;

public class AlertPublisherImpl implements AlertPublisher {

    private final KafkaTemplate<String, AlertRequest> kafkaTemplate;

    public AlertPublisherImpl(KafkaTemplate<String, AlertRequest> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    @Override
    public void publish(AlertRequest alert) {
        kafkaTemplate.send("alerts", alert.getServiceName(), alert);
    }

}
