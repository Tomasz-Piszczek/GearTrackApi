package com.example.geartrackapi.infrastructure.events.incoming;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.sqs.SqsAsyncClient;
import software.amazon.awssdk.services.sqs.model.DeleteMessageRequest;
import software.amazon.awssdk.services.sqs.model.Message;
import software.amazon.awssdk.services.sqs.model.ReceiveMessageRequest;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

@Service
@RequiredArgsConstructor
@Slf4j
public class SqsMessageProcessor {

    private final SqsAsyncClient sqsAsyncClient;

    public void processMessagesWithBatchingParallel(String queueUrl, 
                                                   Integer maxNumberOfMessages,
                                                   Integer maxBatchesPerRun,
                                                   Consumer<Message> messageConsumer,
                                                   String operationName) {
        try {
            for (int batch = 0; batch < maxBatchesPerRun; batch++) {
                ReceiveMessageRequest receiveRequest = ReceiveMessageRequest.builder()
                        .queueUrl(queueUrl)
                        .maxNumberOfMessages(maxNumberOfMessages)
                        .waitTimeSeconds(1)
                        .build();

                CompletableFuture<List<Message>> messagesFuture = sqsAsyncClient.receiveMessage(receiveRequest)
                        .thenApply(response -> response.messages());

                List<Message> messages = messagesFuture.join();
                
                if (messages.isEmpty()) {
                    break;
                }

                log.info("Processing {} messages for operation: {}", messages.size(), operationName);

                messages.parallelStream().forEach(message -> {
                    try {
                        messageConsumer.accept(message);
                        deleteMessage(queueUrl, message);
                    } catch (Exception e) {
                        log.error("Error processing message: {}", e.getMessage(), e);
                    }
                });
            }
        } catch (Exception e) {
            log.error("Error in message processing for operation {}: {}", operationName, e.getMessage(), e);
        }
    }

    private void deleteMessage(String queueUrl, Message message) {
        try {
            DeleteMessageRequest deleteRequest = DeleteMessageRequest.builder()
                    .queueUrl(queueUrl)
                    .receiptHandle(message.receiptHandle())
                    .build();

            sqsAsyncClient.deleteMessage(deleteRequest).join();
            log.debug("Deleted message with receipt handle: {}", message.receiptHandle());
        } catch (Exception e) {
            log.error("Failed to delete message: {}", e.getMessage());
        }
    }
}