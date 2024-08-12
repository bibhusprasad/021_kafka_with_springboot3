package com.bibhu.learn.kafka.orderservice.service;

import com.bibhu.learn.kafka.orderservice.message.OrderCreated;
import com.bibhu.learn.kafka.orderservice.message.OrderDispatched;

import com.bibhu.learn.kafka.orderservice.util.TestEventData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.kafka.core.KafkaTemplate;

import java.util.concurrent.CompletableFuture;

import static java.util.UUID.randomUUID;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class OrderDispatchServiceTest {

    private OrderDispatchService orderDispatchService;
    private KafkaTemplate kafkaProducerMock;

    @BeforeEach
    void setUp() {
        kafkaProducerMock = mock(KafkaTemplate.class);
        orderDispatchService = new OrderDispatchService(kafkaProducerMock);
    }

    @Test
    void process_success() throws Exception {
        when(kafkaProducerMock.send(eq("order.dispatched"), any(OrderDispatched.class))).thenReturn(mock(CompletableFuture.class));
        OrderCreated orderCreated = TestEventData.buildOrderCreatedEvent(randomUUID(), randomUUID().toString());
        orderDispatchService.process(orderCreated);
        verify(kafkaProducerMock, times(1)).send(eq("order.dispatched"), any(OrderDispatched.class));
    }

    @Test
    void process_producerThrowsException() throws Exception {
        OrderCreated orderCreated = TestEventData.buildOrderCreatedEvent(randomUUID(), randomUUID().toString());
        doThrow(new RuntimeException("Produce Failure")).when(kafkaProducerMock).send(eq("order.dispatched"), any(OrderDispatched.class));
        Exception exception = assertThrows(RuntimeException.class, () -> orderDispatchService.process(orderCreated));
        verify(kafkaProducerMock, times(1)).send(eq("order.dispatched"), any(OrderDispatched.class));
        assertThat(exception.getMessage()).isEqualTo("Produce Failure");
    }
}
