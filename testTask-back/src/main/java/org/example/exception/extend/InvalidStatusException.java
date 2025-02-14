package org.example.exception.extend;

import org.example.controlleradvice.Errors;
import org.example.exception.BadDataException;

public class InvalidStatusException extends BadDataException {
    public InvalidStatusException(String message, Errors errorCode) {
        super(message, errorCode);
    }
}
