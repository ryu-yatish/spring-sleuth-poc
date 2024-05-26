package com.harivemula.products.listeners;
import lombok.RequiredArgsConstructor;
import org.springframework.cloud.sleuth.CurrentTraceContext;
import org.springframework.cloud.sleuth.TraceContext;
import org.springframework.cloud.sleuth.Tracer;
import org.springframework.cloud.sleuth.Span;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.aws.messaging.listener.annotation.SqsListener;
import org.springframework.messaging.handler.annotation.Headers;
import org.springframework.stereotype.Component;
import com.harivemula.products.services.ItemService;

import java.util.Map;
import java.util.Objects;

@Component
@Slf4j
@RequiredArgsConstructor
public class SQSConsumer {

    private final Tracer tracer;
    private final CurrentTraceContext currentTraceContext;
    private final ItemService itemService;
    @SqsListener("${cloud.aws.queue.name}")
    public void receiveMessage(Map<String, String> message, @Headers Map<String, String> headers) {
        log.info("SQS Message Received : {}", message);
        String traceId = headers.get("traceId");
        String spanId = headers.get("spanId");
        CurrentTraceContext currentContext = currentTraceContext;
        if (traceId != null && spanId != null) {
            assert currentContext != null;
            TraceContext newTraceContext = tracer.traceContextBuilder()
                    .traceId(traceId)
                    .spanId(spanId)
                    .sampled(true)
                    .build();

            try (CurrentTraceContext.Scope scope = currentContext.newScope(newTraceContext)) {
                Span newSpan = this.tracer.nextSpan().name("sqsListenerSpan").start();
                try (Tracer.SpanInScope ws = tracer.withSpan(newSpan)) {
                    if(message.get("Records").equals("all"))itemService.printAllItems();
                    else itemService.printItemById(message.get("Records"));
                    log.info("Processing message with traceId: {} and spanId: {}", traceId, spanId);
                } catch (Exception e) {
                    newSpan.error(e);
                    log.error("Error processing message", e);
                } finally {
                    newSpan.end();
                }
            }catch (Exception e) {
                log.error("Error processing message", e);
            }
        } else {
            log.warn("Missing traceId or spanId in headers");
        }
    }
}