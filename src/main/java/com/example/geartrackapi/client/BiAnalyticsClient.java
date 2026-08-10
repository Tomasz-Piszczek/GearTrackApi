package com.example.geartrackapi.client;

import com.example.geartrackapi.controller.payroll.dto.EmployeeHoursDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.List;
import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class BiAnalyticsClient {

    private final RestTemplate restTemplate;
    private final BiAnalyticsClientConfig config;

    public <T> T executeRequest(String path, HttpMethod method, Object body,
                                 ParameterizedTypeReference<T> responseType,
                                 String jwtToken, Map<String, String> queryParams) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(jwtToken);

        UriComponentsBuilder uriBuilder = UriComponentsBuilder
            .fromHttpUrl(config.getBaseUrl() + path);
        queryParams.forEach(uriBuilder::queryParam);

        HttpEntity<Object> entity = new HttpEntity<>(body, headers);

        log.info("[BiAnalyticsClient] Sending {} request to {}", method, uriBuilder.toUriString());

        ResponseEntity<T> response = restTemplate.exchange(
            uriBuilder.toUriString(), method, entity, responseType);
        return response.getBody();
    }

    public List<EmployeeHoursDto> getEmployeeHours(List<String> employeeNames,
                                                    int year, int month, String jwtToken) {
        return executeRequest(
            "/api/employees/hours",
            HttpMethod.POST,
            employeeNames,
            new ParameterizedTypeReference<>() {},
            jwtToken,
            Map.of("year", String.valueOf(year), "month", String.valueOf(month))
        );
    }
}
