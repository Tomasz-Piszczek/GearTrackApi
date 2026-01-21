package com.example.geartrackapi.infrastructure.events.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class LocalStackService {

    @Value("${sqs.launch-localstack:false}")
    private boolean launchLocalStack;

    public void setupLocalStack() {
        if (!launchLocalStack) {
            log.info("LocalStack setup skipped - GearTrackApi only consumes SQS events");
            return;
        }
        log.info("LocalStack setup skipped - GearTrackApi only consumes SQS events");
    }

    public void shutdownLocalStack() {
        if (!launchLocalStack) {
            return;
        }
        log.info("LocalStack shutdown skipped - GearTrackApi only consumes SQS events");
    }
}