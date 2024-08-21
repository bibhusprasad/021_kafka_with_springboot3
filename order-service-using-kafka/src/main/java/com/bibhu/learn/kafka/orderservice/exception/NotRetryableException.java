package com.bibhu.learn.kafka.orderservice.exception;

public class NotRetryableException extends RuntimeException {

    public NotRetryableException(Exception exception) {
        super(exception);
    }
}