package com.zor07.nofapp.api.v1;

import com.zor07.nofapp.exception.IllegalAuthorizationHeaderException;
import com.zor07.nofapp.exception.IllegalResourceAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;

import javax.persistence.EntityNotFoundException;

@ControllerAdvice
public class RestControllerAdvice {

    @ResponseStatus(value = HttpStatus.NO_CONTENT, reason = "Resource is not found")
    @ExceptionHandler(EntityNotFoundException.class)
    public void handleNotFound() {
        // Nothing to do
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler({IllegalArgumentException.class, IllegalResourceAccessException.class})
    public void handleBadRequest() {
        // Nothing to do
    }

    @ResponseStatus(HttpStatus.FORBIDDEN)
    @ExceptionHandler(IllegalAuthorizationHeaderException.class)
    public void handleForbidden() {
        // Nothing to do
    }
}
