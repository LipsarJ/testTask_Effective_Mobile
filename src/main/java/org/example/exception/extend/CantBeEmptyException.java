package org.example.exception.extend;

import org.example.controlleradvice.Errors;
import org.example.exception.BadDataException;

public class CantBeEmptyException extends BadDataException {
    public CantBeEmptyException(String message, Errors errorCode) {
        super(message, errorCode);
    }
}
