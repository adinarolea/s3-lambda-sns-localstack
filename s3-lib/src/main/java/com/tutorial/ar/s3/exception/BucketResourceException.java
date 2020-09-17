package com.tutorial.ar.s3.exception;

public class BucketResourceException extends RuntimeException {

    public BucketResourceException(String message) {
        super(message);
    }

    public BucketResourceException(String message, Throwable cause) {
        super(message, cause);
    }
}
