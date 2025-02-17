package org.example.exception.extend;

import org.example.exception.NotFoundException;

public class TaskNotFoundException extends NotFoundException {
    public TaskNotFoundException(String message) {
        super(message);
    }
}
