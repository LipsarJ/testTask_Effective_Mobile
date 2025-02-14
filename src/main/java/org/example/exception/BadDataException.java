package org.example.exception;

import org.example.controlleradvice.Errors;

public class BadDataException extends RuntimeException{
    private final Errors errorCode;
    public BadDataException(String message, Errors errorCode) {
        super(message);
        this.errorCode = errorCode;
    }

    public Errors getErrorCode() {
        return errorCode;
    }
}
