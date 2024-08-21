package com.bibhu.learn.kafka.orderservice.service;

import com.bibhu.learn.kafka.orderservice.client.StockServiceClient;
import com.bibhu.learn.kafka.orderservice.message.DispatchCompleted;
import com.bibhu.learn.kafka.orderservice.message.DispatchPreparing;
import com.bibhu.learn.kafka.orderservice.message.OrderCreated;
import com.bibhu.learn.kafka.orderservice.message.OrderDispatched;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
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
    private final StockServiceClient stockServiceClient;

    public void process(String key, OrderCreated orderPayload) throws Exception {

        String available = stockServiceClient.checkAvailability(orderPayload.getItem());

        if(Boolean.valueOf(available)){

            DispatchPreparing dispatchPreparing = DispatchPreparing.builder()
                    .orderId(orderPayload.getOrderId())
                    .build();
            kafkaProducer.send(DISPATCH_TRACKING_TOPIC, key, dispatchPreparing).get();

            OrderDispatched orderDispatched = OrderDispatched.builder()
                    .orderId(orderPayload.getOrderId())
                    .processedById(APPLICATION_ID)
                    .notes("Dispatched: " + orderPayload.getItem())
                    .build();
            kafkaProducer.send(ORDER_DISPATCHED_TOPIC, key, orderDispatched).get();

            DispatchCompleted dispatchCompleted = DispatchCompleted.builder()
                    .orderId(orderPayload.getOrderId())
                    .dispatchedDate(LocalDate.now().toString())
                    .build();
            kafkaProducer.send(DISPATCH_TRACKING_TOPIC, key, dispatchCompleted).get();

            log.info("Send messages - key: {} - orderId: {} - processedById: {}", key, orderPayload.getOrderId(), APPLICATION_ID);

        } else {
            log.info("Item {}", orderPayload.getItem() + " is unavailable.");
        }
    }
}
