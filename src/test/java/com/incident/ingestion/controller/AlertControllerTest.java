package com.incident.ingestion.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.incident.ingestion.exception.GlobalExceptionHandler;
import com.incident.ingestion.model.AlertRequest;
import com.incident.ingestion.model.Severity;
import com.incident.ingestion.model.AlertEvent;
import com.incident.ingestion.constants.ApiConstants;
import com.incident.ingestion.service.AlertService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;
import org.springframework.web.servlet.mvc.method.annotation.ExceptionHandlerExceptionResolver;

import jakarta.servlet.ServletException;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class AlertControllerTest {

    private MockMvc mockMvc;

    @Mock
    private AlertService alertService;

    @InjectMocks
    private AlertController alertController;

    private ObjectMapper objectMapper;

    private AlertRequest validAlert;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this); // For unit test, warning is safe to ignore
        objectMapper = new ObjectMapper();

        // Register validator and exception resolver for @Valid to work

        LocalValidatorFactoryBean validator = new LocalValidatorFactoryBean();
        validator.afterPropertiesSet();

        ExceptionHandlerExceptionResolver exceptionResolver = new ExceptionHandlerExceptionResolver();
        exceptionResolver.afterPropertiesSet();

        mockMvc = MockMvcBuilders.standaloneSetup(alertController)
                .setValidator(validator)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();

        validAlert = new AlertRequest();
        validAlert.setServiceName("test-service");
        validAlert.setSeverity(Severity.CRITICAL);
        validAlert.setMessage("Test alert message");
        validAlert.setTimestamp(System.currentTimeMillis());
    }

    @Test
    void ingestAlerts_ValidRequest_ReturnsAccepted() throws Exception {
        UUID alertId = UUID.randomUUID();
        AlertEvent alertEvent = new AlertEvent();
        alertEvent.setAlertId(alertId);
        Mockito.when(alertService.ingest(Mockito.any(AlertRequest.class),
                        Mockito.eq("test-alert-001")))
                .thenReturn(alertEvent);

        ResultActions result = mockMvc.perform(post("/api/alerts")
                .header(ApiConstants.IDEMPOTENCY_KEY_HEADER, "test-alert-001")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(validAlert)));

        result.andExpect(status().isAccepted())
                .andExpect(jsonPath("$.alertId").value(alertId.toString()))
                .andExpect(jsonPath("$.status").value("ACCEPTED"))
                .andExpect(jsonPath("$.message").value("Alert accepted for processing"));
        Mockito.verify(alertService).ingest(Mockito.any(AlertRequest.class),
                Mockito.eq("test-alert-001"));
    }

    @Test
    void ingestAlerts_InvalidRequest_ReturnsBadRequest() throws Exception {
        AlertRequest invalidAlert = new AlertRequest();
        // missing required fields
        ResultActions result = mockMvc.perform(post("/api/alerts")
                .header(ApiConstants.IDEMPOTENCY_KEY_HEADER, "test-alert-invalid-001")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidAlert)));

        result.andExpect(status().isBadRequest())
              .andExpect(result1 -> {
                  Throwable ex = result1.getResolvedException();
                  // Unwrap ServletException if present
                  if (ex instanceof ServletException && ex.getCause() != null) {
                      ex = ex.getCause();
                  }
                  assertTrue(ex instanceof org.springframework.web.bind.MethodArgumentNotValidException);
              });
    }
}
