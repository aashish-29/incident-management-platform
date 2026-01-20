package com.incident.ingestion.controller;

import com.incident.ingestion.model.AlertRequest;
import com.incident.ingestion.service.AlertPublisher;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/alerts")
public class AlertController {


    private final AlertPublisher alertPublisher;

    @Autowired
    public AlertController(AlertPublisher alertPublisher) {
        this.alertPublisher = alertPublisher;
    }

    @PostMapping
    public ResponseEntity<String> ingestAlerts(@Valid  @RequestBody  AlertRequest alert) {

       alertPublisher.publish(alert);

       return ResponseEntity.ok("Alert accepted");
    }

}
