package com.harivemula.inventory.service;
import com.amazonaws.services.sqs.model.MessageAttributeValue;
import org.springframework.cloud.sleuth.Tracer;
import io.opentelemetry.context.propagation.TextMapPropagator;
import io.opentelemetry.context.Context;

import com.amazonaws.services.sqs.AmazonSQS;
import com.amazonaws.services.sqs.model.SendMessageRequest;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.aws.messaging.core.QueueMessagingTemplate;
import org.springframework.cloud.sleuth.TraceContext;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
public class SQSEventPublisher {

    @Autowired
    private AmazonSQS amazonSQS;

    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private Tracer tracer;

    private final QueueMessagingTemplate queueMessagingTemplate;

    public void publishEvent(JsonNode message) {
        log.info("Generating event : {}", message);


        Map<String, MessageAttributeValue> messageAttributes = new HashMap<>();


        try {
            TraceContext traceContext = tracer.currentSpan().context();
            MessageAttributeValue value1 =new MessageAttributeValue();
            value1.setStringValue(traceContext.traceId());
            value1.setDataType("String");

            MessageAttributeValue value2 =new MessageAttributeValue();
            value2.setStringValue(traceContext.spanId());
            value2.setDataType("String");

            messageAttributes.put("traceId", value1);
            messageAttributes.put("spanId", value2);

            log.info(messageAttributes.toString());
            SendMessageRequest sendMessageRequest = new SendMessageRequest()
                    .withQueueUrl("https://sqs.us-east-1.amazonaws.com/565433475303/sample.fifo")
                    .withMessageBody(objectMapper.writeValueAsString(message))
                    .withMessageGroupId("Sample123Message")
                    .withMessageDeduplicationId(UUID.randomUUID().toString())
                    .withMessageAttributes(messageAttributes);

            amazonSQS.sendMessage(sendMessageRequest);
            log.info("Event has been published in SQS.");
        } catch (JsonProcessingException e) {
            log.error("JsonProcessingException e : {} and stacktrace : {}", e.getMessage(), e);
        } catch (Exception e) {
            log.error("Exception occurred while pushing event to SQS : {} and stacktrace : {}", e.getMessage(), e);
        } finally {
            //
        }

    }
}