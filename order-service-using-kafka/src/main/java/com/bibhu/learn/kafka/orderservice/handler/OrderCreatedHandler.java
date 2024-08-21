package com.bibhu.learn.kafka.orderservice.handler;

import com.bibhu.learn.kafka.orderservice.exception.NotRetryableException;
import com.bibhu.learn.kafka.orderservice.exception.RetryableException;
import com.bibhu.learn.kafka.orderservice.message.OrderCreated;
import com.bibhu.learn.kafka.orderservice.service.OrderDispatchService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

@Slf4j
@AllArgsConstructor
@Component
public class OrderCreatedHandler {

    private OrderDispatchService orderDispatchService;

    @KafkaListener(
            id = "orderConsumerClient",
            topics = "order.created",
            groupId = "dispatch.order.created.consumer2",
            containerFactory = "kafkaListenerContainerFactory"
    )
    public void listen(
            @Header(KafkaHeaders.RECEIVED_PARTITION) Integer partition,
            @Header(KafkaHeaders.RECEIVED_KEY) String key,
            @Payload OrderCreated orderPayload
    ){
        log.info("OrderCreatedHandler received order - partition: {} - key: {} - payload: {}", partition, key, orderPayload);
        try {
            orderDispatchService.process(key, orderPayload);
        } catch (RetryableException e) {
            log.warn("Retryable exception: {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("NotRetryable exception: {}", e.getMessage());
            throw new NotRetryableException(e);
        }
    }
}
