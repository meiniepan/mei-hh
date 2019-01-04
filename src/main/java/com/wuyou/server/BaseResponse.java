package com.wuyou.server;

import org.springframework.http.HttpStatus;

public class BaseResponse<T> {
    private int code;
    private String message;
    private T data;

    public int getCode() {
        return this.code;
    }

    public String getMessage() {
        return this.message;
    }

    public T getData() {
        return this.data;
    }

    public BaseResponse(T data, HttpStatus status) {
        this.data = data;
        this.code = status.value();
    }

    public BaseResponse(T data) {
        this(data, HttpStatus.OK);
    }

    public BaseResponse(HttpStatus status) {
        this.code = status.value();
    }

    public BaseResponse(HttpStatus status, String message) {
        this.code = status.value();
        this.message = message;
    }

    public BaseResponse(T data, HttpStatus status, String message) {
        this.data = data;
        this.code = status.value();
        this.message = message;
    }
}
