package com.incident.ingestion.controller;

import com.incident.ingestion.model.AlertRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/alerts")
@RequiredArgsConstructor
public class AlertController {

    private final KafkaTemplate<String, AlertRequest> kafkaTemplate;

   @PostMapping
    public ResponseEntity<String> ingestAlerts(@Valid  @RequestBody  AlertRequest alert) {
       kafkaTemplate.send(
               "alerts",
               alert.getServiceName(),
               alert
       );

       return ResponseEntity.ok("Alert accepted");
    }

}
