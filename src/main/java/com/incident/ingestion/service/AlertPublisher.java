package com.incident.ingestion.service;

import com.incident.ingestion.model.AlertEvent;

import java.util.concurrent.CompletableFuture;

public interface AlertPublisher {

    CompletableFuture<Void> publish(AlertEvent alertEvent);

}
