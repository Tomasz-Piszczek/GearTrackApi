package com.example.geartrackapi.infrastructure.events.incoming;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class SqsIncomingEventsListener {
    
    @Value("${sqs.url.read.employeeChangeQueue:}")
    private String employeeChangeQueueUrl;

    @Value("${sqs.limits.maxNumberOfMessages:10}")
    private Integer maxNumberOfMessages;

    @Value("${sqs.limits.maxBatchesPerRun:5}")
    private Integer maxBatchesPerRun;

    private final SqsMessageProcessor messageProcessor;
    private final SqsIncomingEventHandler incomingEventHandler;

    @Scheduled(fixedDelayString = "${sqs.rate:10000}")
    public void listenEmployeeChanges() {
        processIncomingEmployeeChangeEvents();
    }

    @SneakyThrows
    public void processIncomingEmployeeChangeEvents() {

        messageProcessor.processMessagesWithBatchingParallel(
                employeeChangeQueueUrl,
                maxNumberOfMessages,
                maxBatchesPerRun,
                incomingEventHandler::consumeEmployeeChangeEvent,
                "processEmployeeChangeEvents"
        );
    }
}