package org.example.controlleradvice;

import org.example.exception.TokenRefreshException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.ArrayList;
import java.util.List;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ValidationErrorResponse> handleValidationException(MethodArgumentNotValidException ex) {
        List<ValidationErrorResponse.ValidationError> errors = new ArrayList<>();

        for (FieldError fieldError : ex.getBindingResult().getFieldErrors()) {
            String fieldName = fieldError.getField();
            String message = fieldError.getDefaultMessage();
            errors.add(new ValidationErrorResponse.ValidationError(fieldName, message));
        }

        for (ObjectError objectError : ex.getBindingResult().getGlobalErrors()) {
            String message = objectError.getDefaultMessage();
            errors.add(new ValidationErrorResponse.ValidationError("", message));
        }

        ValidationErrorResponse errorResponse = new ValidationErrorResponse("Ошибка валидации", errors);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }

    public record ValidationErrorResponse(
            String message,
            List<ValidationError> errors
    ) {
        public record ValidationError(
                String field,
                String message
        ) {
        }
    }


    @ExceptionHandler(TokenRefreshException.class)
    public ResponseEntity<SimpleResponse> handleTokenRefreshException(TokenRefreshException ex) {
        SimpleResponse simpleResponse = new SimpleResponse(ex.getMessage(), null);
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(simpleResponse);
    }

}

