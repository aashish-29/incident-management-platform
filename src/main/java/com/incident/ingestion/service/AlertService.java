package com.incident.ingestion.service;

import com.incident.ingestion.model.AlertEvent;
import com.incident.ingestion.model.AlertEventType;
import com.incident.ingestion.model.AlertRequest;
import com.incident.ingestion.model.AlertSource;
import org.springframework.stereotype.Service;

import java.util.UUID;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.CompletionException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import com.incident.ingestion.exception.AlertPublishException;
import com.incident.ingestion.exception.InvalidIdempotencyKeyException;

@Service
public class AlertService {

    private final AlertPublisher alertPublisher;

    public AlertService(AlertPublisher alertPublisher) {
        this.alertPublisher = alertPublisher;
    }

    public AlertEvent ingest(AlertRequest alertRequest, String idempotencyKey) {
        if (idempotencyKey == null || idempotencyKey.isBlank()) {
            throw new InvalidIdempotencyKeyException("Idempotency-Key must not be blank");
        }

        AlertEvent alertEvent = new AlertEvent();
        UUID alertId = UUID.nameUUIDFromBytes(
                ("incident-management:" + idempotencyKey)
                        .getBytes(StandardCharsets.UTF_8)
        );

        alertEvent.setAlertId(alertId);
        alertEvent.setServiceName(alertRequest.getServiceName());
        alertEvent.setSeverity(alertRequest.getSeverity());
        alertEvent.setMessage(alertRequest.getMessage());
        alertEvent.setTimestamp(alertRequest.getTimestamp());
        alertEvent.setSource(AlertSource.REST_API);
        alertEvent.setEventType(AlertEventType.ALERT_CREATED);

        try {
            alertPublisher.publish(alertEvent).get(5, TimeUnit.SECONDS);
        } catch (InterruptedException ex) {
            Thread.currentThread().interrupt();
            throw new AlertPublishException("Alert publishing was interrupted", ex);
        } catch (ExecutionException | TimeoutException | CompletionException ex) {
            throw new AlertPublishException("Unable to publish alert to Kafka", ex);
        }

        return alertEvent;
    }
}
