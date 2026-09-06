package com.incident.ingestion.model;

import lombok.Data;

import java.util.UUID;

@Data
public class AlertEvent {

    private UUID alertId;

    private String serviceName;

    private Severity severity;

    private String message;

    private Long timestamp;

    private AlertSource source;

    private AlertEventType eventType;
}
