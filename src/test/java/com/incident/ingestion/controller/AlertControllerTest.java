package com.incident.ingestion.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.incident.ingestion.exception.GlobalExceptionHandler;
import com.incident.ingestion.model.AlertRequest;
import com.incident.ingestion.model.Severity;
import com.incident.ingestion.service.AlertPublisher;
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

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class AlertControllerTest {

    private MockMvc mockMvc;

    @Mock
    private AlertPublisher alertPublisher;

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
    void ingestAlerts_ValidRequest_ReturnsOk() throws Exception {
        ResultActions result = mockMvc.perform(post("/api/alerts")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(validAlert)));

        result.andExpect(status().isOk())
                .andExpect(content().string("Alert accepted"));
        Mockito.verify(alertPublisher).publish(Mockito.any(AlertRequest.class));
    }

    @Test
    void ingestAlerts_InvalidRequest_ReturnsBadRequest() throws Exception {
        AlertRequest invalidAlert = new AlertRequest();
        // missing required fields
        ResultActions result = mockMvc.perform(post("/api/alerts")
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
