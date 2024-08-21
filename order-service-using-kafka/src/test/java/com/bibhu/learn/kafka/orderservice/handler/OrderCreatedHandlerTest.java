package com.bibhu.learn.kafka.orderservice.handler;

import com.bibhu.learn.kafka.orderservice.message.OrderCreated;
import com.bibhu.learn.kafka.orderservice.service.OrderDispatchService;
import com.bibhu.learn.kafka.orderservice.util.TestEventData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static java.util.UUID.randomUUID;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

public class OrderCreatedHandlerTest {

    private OrderCreatedHandler orderCreatedHandler;
    private OrderDispatchService orderDispatchServiceMock;

    @BeforeEach
    void setUp() {
        orderDispatchServiceMock = mock(OrderDispatchService.class);
        orderCreatedHandler = new OrderCreatedHandler(orderDispatchServiceMock);
    }

    @Test
    void listen_success() throws Exception{
        String key = randomUUID().toString();
        OrderCreated orderCreated = TestEventData.buildOrderCreatedEvent(randomUUID(), randomUUID().toString());
        orderCreatedHandler.listen(0, key, orderCreated);
        verify(orderDispatchServiceMock, times(1)).process(key, orderCreated);
    }

    @Test
    void listen_serviceThrowsException() throws Exception{
        String key = randomUUID().toString();
        OrderCreated orderCreated = TestEventData.buildOrderCreatedEvent(randomUUID(), randomUUID().toString());
        doThrow(new RuntimeException("Service Failure")).when(orderDispatchServiceMock).process(key, orderCreated);
        orderCreatedHandler.listen(0, key, orderCreated);
        verify(orderDispatchServiceMock, times(1)).process(key, orderCreated);
    }

}
