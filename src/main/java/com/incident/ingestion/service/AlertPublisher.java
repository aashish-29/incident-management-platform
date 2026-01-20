package com.incident.ingestion.service;

import com.incident.ingestion.model.AlertRequest;

public interface AlertPublisher {

    void publish(AlertRequest alert);

}
