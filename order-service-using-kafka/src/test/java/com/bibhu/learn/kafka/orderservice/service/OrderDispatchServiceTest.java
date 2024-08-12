package com.bibhu.learn.kafka.orderservice.service;

import com.bibhu.learn.kafka.orderservice.message.OrderCreated;
import com.bibhu.learn.kafka.orderservice.util.TestEventData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static java.util.UUID.randomUUID;

public class OrderDispatchServiceTest {

    private OrderDispatchService orderDispatchService;

    @BeforeEach
    void setUp() {
        orderDispatchService = new OrderDispatchService();
    }

    @Test
    void process() {
        OrderCreated orderCreated = TestEventData.buildOrderCreatedEvent(randomUUID(), randomUUID().toString());
        orderDispatchService.process(orderCreated);
    }
}
