package com.incident.ingestion.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class AlertRequest {

    @NotBlank
    private String serviceName;

    @NotNull
    private Severity severity; // CRITICAL, HIGH, MEDIUM

    @NotBlank
    private String message;

    @NotNull
    private Long timestamp;
}
