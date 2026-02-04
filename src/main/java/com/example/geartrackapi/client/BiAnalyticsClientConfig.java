package com.example.geartrackapi.client;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "bi-analytics")
@Data
public class BiAnalyticsClientConfig {
    private String baseUrl;
    private int timeout;
}
