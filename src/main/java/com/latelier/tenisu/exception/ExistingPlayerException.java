package com.latelier.tenisu.exception;

public class ExistingPlayerException extends RuntimeException {
    public ExistingPlayerException(String message) {
        super(message);
    }

}
