package com.codefarm.productclient.exception;

public class MyCustomExceptionServerIssue extends RuntimeException{
    public MyCustomExceptionServerIssue(String s) {
        super(s);
    }
}
