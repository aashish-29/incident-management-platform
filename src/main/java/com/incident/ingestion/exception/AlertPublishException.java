package com.incident.ingestion.exception;

public class AlertPublishException extends RuntimeException {

    public AlertPublishException(String message, Throwable cause) {
        super(message, cause);
    }
}
