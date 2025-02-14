package org.example.exception.extend;

import org.example.controlleradvice.Errors;
import org.example.exception.BadDataException;

public class InvalidPriorityException extends BadDataException {
    public InvalidPriorityException(String message, Errors errorCode) {
        super(message, errorCode);
    }
}
