package com.dataquadinc.exceptions;

import com.dataquadinc.commons.ApiResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {


    @ExceptionHandler(InvalidFileTypeException.class)
    public ResponseEntity<ApiResponse<Void>> handleInvalidFileTypeException(InvalidFileTypeException ex){

      return ResponseEntity.status(HttpStatus.CONFLICT).body(
              ApiResponse.error(ex.getMessage(), HttpStatus.CONFLICT)
      );
    }

    @ExceptionHandler(FeignClientException.class)
    public ResponseEntity<ApiResponse<Void>> handleFeignClientException(FeignClientException ex){

        return ResponseEntity.status(HttpStatus.CONFLICT).body(
                ApiResponse.error(ex.getMessage(), HttpStatus.CONFLICT)
        );
    }
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ApiResponse<Void>> handleResourceNotFoundException(ResourceNotFoundException ex){

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                ApiResponse.error(ex.getMessage(), HttpStatus.NOT_FOUND)
        );
    }
}
