package com.bibhu.learn.kafka.orderservice.handler;

import com.bibhu.learn.kafka.orderservice.message.OrderCreated;
import com.bibhu.learn.kafka.orderservice.service.OrderDispatchService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Slf4j
@AllArgsConstructor
@Component
public class OrderCreatedHandler {

    private OrderDispatchService orderDispatchService;

    @KafkaListener(
            id = "orderConsumerClient",
            topics = "order.created",
            groupId = "dispatch.order.created.consumer"
    )
    public void listen(OrderCreated orderPayload) {
        log.info("OrderCreatedHandler received order payload: {}", orderPayload);
        orderDispatchService.process(orderPayload);
    }
}
