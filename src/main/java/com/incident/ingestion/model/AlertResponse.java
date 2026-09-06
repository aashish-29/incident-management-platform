package com.incident.ingestion.model;

import java.util.UUID;

public record AlertResponse(
        UUID alertId,
        String status,
        String message
) {
}
