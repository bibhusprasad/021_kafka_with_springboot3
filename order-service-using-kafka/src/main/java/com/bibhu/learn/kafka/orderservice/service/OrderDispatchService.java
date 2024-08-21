package com.bibhu.learn.kafka.orderservice.service;

import com.bibhu.learn.kafka.orderservice.message.DispatchPreparing;
import com.bibhu.learn.kafka.orderservice.message.OrderCreated;
import com.bibhu.learn.kafka.orderservice.message.OrderDispatched;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.util.UUID;

import static java.util.UUID.randomUUID;

@Slf4j
@Service
@AllArgsConstructor
public class OrderDispatchService {

    private static final String ORDER_DISPATCHED_TOPIC = "order.dispatched";
    private static final String DISPATCH_TRACKING_TOPIC = "dispatch.tracking";
    private static final UUID APPLICATION_ID = randomUUID();

    private final KafkaTemplate<String, Object> kafkaProducer;

    public void process(String key, OrderCreated orderPayload) throws Exception {
        OrderDispatched orderDispatched = OrderDispatched.builder()
                .orderId(orderPayload.getOrderId())
                .processedById(APPLICATION_ID)
                .notes("Dispatched : " + orderPayload.getItem())
                .build();

        kafkaProducer.send(ORDER_DISPATCHED_TOPIC, key, orderDispatched).get();

        log.info("Send messages - key: {} - orderId: {} - processedById: {}", key, orderPayload.getOrderId(), APPLICATION_ID);

        DispatchPreparing dispatchPreparing = DispatchPreparing.builder()
                .orderId(orderPayload.getOrderId())
                .build();
        kafkaProducer.send(DISPATCH_TRACKING_TOPIC, key, dispatchPreparing).get();
    }
}
