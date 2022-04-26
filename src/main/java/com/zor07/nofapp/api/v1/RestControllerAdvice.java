package com.zor07.nofapp.api.v1;

import com.zor07.nofapp.exception.IllegalResourceAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;

@ControllerAdvice
public class RestControllerAdvice {

    @ResponseStatus(value = HttpStatus.NOT_FOUND, reason = "Resource is not found")
    @ExceptionHandler(EmptyResultDataAccessException.class)
    public void handleNotFound() {
        // Nothing to do
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(IllegalResourceAccessException.class)
    public void handleBadRequest() {
        // Nothing to do
    }
}
