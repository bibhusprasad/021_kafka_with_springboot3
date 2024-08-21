package com.bibhu.learn.kafka.orderservice.service;

import com.bibhu.learn.kafka.orderservice.message.DispatchPreparing;
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
import static org.mockito.Mockito.verifyNoMoreInteractions;
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
        String key = randomUUID().toString();
        when(kafkaProducerMock.send(eq("order.dispatched"), anyString(), any(OrderDispatched.class))).thenReturn(mock(CompletableFuture.class));
        when(kafkaProducerMock.send(eq("dispatch.tracking"), anyString(), any(DispatchPreparing.class))).thenReturn(mock(CompletableFuture.class));

        OrderCreated testEvent = TestEventData.buildOrderCreatedEvent(randomUUID(), randomUUID().toString());
        orderDispatchService.process(key, testEvent);

        verify(kafkaProducerMock, times(1)).send(eq("order.dispatched"), eq(key), any(OrderDispatched.class));
        verify(kafkaProducerMock, times(1)).send(eq("dispatch.tracking"), eq(key), any(DispatchPreparing.class));
    }

    @Test
    public void process_OrderDispatchedProducerThrowsException() {
        String key = randomUUID().toString();
        OrderCreated orderCreated = TestEventData.buildOrderCreatedEvent(randomUUID(), randomUUID().toString());

        when(kafkaProducerMock.send(eq("order.dispatched"), any(OrderDispatched.class))).thenReturn(mock(CompletableFuture.class));
        doThrow(new RuntimeException("order dispatched producer failure")).when(kafkaProducerMock).send(eq("order.dispatched"), eq(key), any(OrderDispatched.class));

        Exception exception = assertThrows(RuntimeException.class, () -> orderDispatchService.process(key, orderCreated));

        verify(kafkaProducerMock, times(1)).send(eq("order.dispatched"), eq(key), any(OrderDispatched.class));
        verifyNoMoreInteractions(kafkaProducerMock);
        //verify(kafkaProducerMock, times(1)).send(eq("dispatch.tracking"), any(DispatchPreparing.class));
        assertThat(exception.getMessage()).isEqualTo("order dispatched producer failure");
    }

    @Test
    void process_DispatchTrackingProducerThrowsException() {
        String key = randomUUID().toString();
        OrderCreated orderCreated = TestEventData.buildOrderCreatedEvent(randomUUID(), randomUUID().toString());
        when(kafkaProducerMock.send(eq("order.dispatched"), anyString(), any(OrderDispatched.class))).thenReturn(mock(CompletableFuture.class));


        when(kafkaProducerMock.send(eq("dispatch.tracking"), any(DispatchPreparing.class))).thenReturn(mock(CompletableFuture.class));
        doThrow(new RuntimeException("dispatch tracking producer failure")).when(kafkaProducerMock).send(eq("dispatch.tracking"), eq(key), any(DispatchPreparing.class));
        Exception exception = assertThrows(RuntimeException.class, () -> orderDispatchService.process(key, orderCreated));

        verify(kafkaProducerMock, times(1)).send(eq("dispatch.tracking"), eq(key), any(DispatchPreparing.class));
        verify(kafkaProducerMock, times(1)).send(eq("order.dispatched"), eq(key), any(OrderDispatched.class));
        assertThat(exception.getMessage()).isEqualTo("dispatch tracking producer failure");
    }

}
