package com.codefarm.productclient.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class MyCustomExceptionHandler {

    @ExceptionHandler(MyCustomExceptionServerIssue.class)
    public ResponseEntity<String> handleCustomInternalServerError(MyCustomExceptionServerIssue exceptionServerIssue){
        return new ResponseEntity<>(exceptionServerIssue.getMessage(), HttpStatus.NOT_FOUND);
    }
}
