package com.dataquadinc.exceptions;

public class FeignClientException extends RuntimeException {

    public FeignClientException(String message) {
        super(message);
    }
}
