package com.bibhu.learn.kafka.orderservice.handler;

import com.bibhu.learn.kafka.orderservice.service.OrderDispatchService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

public class OrderCreateHandlerTest {

    private OrderCreateHandler orderCreateHandler;
    private OrderDispatchService orderDispatchServiceMock;

    @BeforeEach
    void setUp() {
        orderDispatchServiceMock = mock(OrderDispatchService.class);
        orderCreateHandler = new OrderCreateHandler(orderDispatchServiceMock);
    }

    @Test
    void listen() {
        orderCreateHandler.listen("orderPayload");
        verify(orderDispatchServiceMock, times(1)).process("orderPayload");
    }

}
