package com.bibhu.learn.kafka.trackingservice.util;

import com.bibhu.learn.kafka.trackingservice.message.DispatchPreparing;

import java.util.UUID;


public class TestEventData {

    public static DispatchPreparing buildDispatchPreparingEvent(UUID orderId) {
        return DispatchPreparing.builder()
                .orderId(orderId)
                .build();
    }
}
