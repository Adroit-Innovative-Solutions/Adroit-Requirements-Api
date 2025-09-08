package com.dataquadinc.exceptions;

public class InvalidFileTypeException extends RuntimeException{

    public InvalidFileTypeException(String message) {
        super(message);
    }
}
