package com.bibhu.learn.kafka.orderservice.util;

import com.bibhu.learn.kafka.orderservice.message.OrderCreated;

import java.util.UUID;

public class TestEventData {

    public static OrderCreated buildOrderCreatedEvent(UUID orderId, String item) {
        return OrderCreated.builder()
                .orderId(orderId)
                .item(item)
                .build();
    }
}
