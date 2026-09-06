package com.incident.ingestion.controller;

import com.incident.ingestion.model.AlertRequest;
import com.incident.ingestion.model.AlertEvent;
import com.incident.ingestion.model.AlertResponse;
import com.incident.ingestion.constants.ApiConstants;
import com.incident.ingestion.service.AlertService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(ApiConstants.ALERTS_PATH)
public class AlertController {


    private final AlertService alertService;

    @Autowired
    public AlertController(AlertService alertService) {
        this.alertService = alertService;
    }

    @PostMapping
    public ResponseEntity<AlertResponse> ingestAlerts(
            @RequestHeader(ApiConstants.IDEMPOTENCY_KEY_HEADER) String idempotencyKey,
            @Valid @RequestBody AlertRequest alert) {

       AlertEvent alertEvent = alertService.ingest(alert, idempotencyKey);

       AlertResponse response = new AlertResponse(
               alertEvent.getAlertId(),
               "ACCEPTED",
               "Alert accepted for processing");

       return ResponseEntity.accepted().body(response);
    }

}
