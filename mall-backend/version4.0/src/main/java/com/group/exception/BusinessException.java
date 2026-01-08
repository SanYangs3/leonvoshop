package com.group.exception;


// BusinessException.java
public class BusinessException extends RuntimeException {

    private Integer code;
    private String message;

    public BusinessException(String message) {
        super(message);
        this.message = message;
    }

    // 省略getter/setter
}