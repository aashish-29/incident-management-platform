package com.incident.ingestion;


import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.jdbc.autoconfigure.DataSourceAutoConfiguration;

@SpringBootApplication(exclude = { DataSourceAutoConfiguration.class })
public class AlertIngestionApplication {
	public static void main(String[] args) {
		SpringApplication.run(AlertIngestionApplication.class, args);
	}
}