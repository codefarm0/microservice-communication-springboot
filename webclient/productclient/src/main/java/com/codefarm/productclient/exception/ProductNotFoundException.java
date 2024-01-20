package com.codefarm.productclient.exception;

public class ProductNotFoundException extends RuntimeException{
    public ProductNotFoundException(String messgae) {
        super(messgae);
    }
}
