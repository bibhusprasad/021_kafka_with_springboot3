package com.bibhu.learn.kafka.orderservice.exception;

import org.springframework.web.client.RestClientException;

public class RetryableException extends RuntimeException{

    public RetryableException(final String message) {
        super(message);
    }

    public RetryableException(Exception e) {
        super(e);
    }
}
