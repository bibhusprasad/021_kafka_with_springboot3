package com.bibhu.learn.kafka.orderservice.handler;

import com.bibhu.learn.kafka.orderservice.exception.NotRetryableException;
import com.bibhu.learn.kafka.orderservice.exception.RetryableException;
import com.bibhu.learn.kafka.orderservice.message.OrderCreated;
import com.bibhu.learn.kafka.orderservice.service.OrderDispatchService;
import com.bibhu.learn.kafka.orderservice.util.TestEventData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static java.util.UUID.randomUUID;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertThrows;
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
    void listen_Success() throws Exception {
        String key = randomUUID().toString();
        OrderCreated testEvent = TestEventData.buildOrderCreatedEvent(randomUUID(), randomUUID().toString());
        orderCreatedHandler.listen(0, key, testEvent);
        verify(orderDispatchServiceMock, times(1)).process(key, testEvent);
    }

    @Test
    public void listen_ServiceThrowsException() throws Exception {
        String key = randomUUID().toString();
        OrderCreated testEvent = TestEventData.buildOrderCreatedEvent(randomUUID(), randomUUID().toString());
        doThrow(new RuntimeException("Service failure")).when(orderDispatchServiceMock).process(key, testEvent);

        Exception exception = assertThrows(NotRetryableException.class, () -> orderCreatedHandler.listen(0, key, testEvent));
        assertThat(exception.getMessage(), equalTo("java.lang.RuntimeException: Service failure"));
        verify(orderDispatchServiceMock, times(1)).process(key, testEvent);
    }

    @Test
    public void testListen_ServiceThrowsRetryableException() throws Exception {
        String key = randomUUID().toString();
        OrderCreated testEvent = TestEventData.buildOrderCreatedEvent(randomUUID(), randomUUID().toString());
        doThrow(new RetryableException("Service failure")).when(orderDispatchServiceMock).process(key, testEvent);

        Exception exception = assertThrows(RuntimeException.class, () -> orderCreatedHandler.listen(0, key, testEvent));
        assertThat(exception.getMessage(), equalTo("Service failure"));
        verify(orderDispatchServiceMock, times(1)).process(key, testEvent);
    }

}
