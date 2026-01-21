package com.example.geartrackapi.infrastructure.events.incoming;

import com.example.geartrackapi.infrastructure.events.model.EmployeeChangeEvent;
import com.example.geartrackapi.service.EmployeeEventProcessingService;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.sqs.model.Message;

@Service
@RequiredArgsConstructor
@Slf4j
public class SqsIncomingEventHandler {

    private final ObjectMapper objectMapper;
    private final EmployeeEventProcessingService employeeEventProcessingService;

    public void consumeEmployeeChangeEvent(Message message) {
        try {
            String messageBody = message.body();
            EmployeeChangeEvent event = objectMapper.readValue(messageBody, EmployeeChangeEvent.class);
            
            log.info("Received employee change event: eventId={}, eventType={}, employeeCount={}", 
                    event.getEventId(), event.getEventType(), 
                    event.getEmployees() != null ? event.getEmployees().size() : 0);
            
            employeeEventProcessingService.processEmployeeChangeEvent(event);

        } catch (Exception e) {
            log.error("Failed to process employee change event: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to process employee change event", e);
        }
    }
}