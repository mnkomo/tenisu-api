package com.latelier.tenisu.controller.handler;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import com.latelier.tenisu.dto.ErrorMessage;
import com.latelier.tenisu.exception.NoContentException;
import com.latelier.tenisu.exception.PlayerNotFoundException;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(NoContentException.class)
    public ResponseEntity<ErrorMessage> handleNoContentException(NoContentException ex) {
        HttpStatus noContent = HttpStatus.NO_CONTENT;
        return new ResponseEntity<>(new ErrorMessage(ex.getMessage(), noContent.value()), noContent);
    }

    @ExceptionHandler(PlayerNotFoundException.class)
    public ResponseEntity<ErrorMessage> handlePlayerNotFoundException(PlayerNotFoundException ex) {
        HttpStatus notFound = HttpStatus.NOT_FOUND;
        return new ResponseEntity<>(new ErrorMessage(ex.getMessage(), notFound.value()), notFound);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorMessage> handleGenericException(Exception ex) {
        HttpStatus internalServerError = HttpStatus.INTERNAL_SERVER_ERROR;
        return new ResponseEntity<>(
                new ErrorMessage("An unexpected error occurred: " + ex.getMessage(), internalServerError.value()),
                internalServerError);
    }

}
