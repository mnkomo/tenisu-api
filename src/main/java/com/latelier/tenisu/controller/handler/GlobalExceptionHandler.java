package com.latelier.tenisu.controller.handler;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import com.latelier.tenisu.dto.ErrorMessage;
import com.latelier.tenisu.exception.NoContentException;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(NoContentException.class)
    public ResponseEntity<ErrorMessage> handleNoContentException(NoContentException ex) {
        HttpStatus noContent = HttpStatus.NO_CONTENT;
        return new ResponseEntity<>(new ErrorMessage(ex.getMessage(), noContent.value()), noContent);
    }

}
