package com.zor07.nofapp.exception;

public class IllegalAuthorizationHeaderException  extends RuntimeException {
    public IllegalAuthorizationHeaderException(String message) {
        super(message);
    }
}
