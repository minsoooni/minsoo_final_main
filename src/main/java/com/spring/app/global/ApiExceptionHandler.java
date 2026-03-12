package com.spring.app.global;

import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import jakarta.validation.ConstraintViolationException;

@RestControllerAdvice
public class ApiExceptionHandler {

    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<?> handleMissingParam(MissingServletRequestParameterException e) {
        return ResponseEntity.badRequest().body(Map.of(
                "error", "Bad Request",
                "message", e.getParameterName() + " is required"
        ));
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<?> handleConstraintViolation(ConstraintViolationException e) {
        // message 예: "checkEmail.email: must be a well-formed email address"
        return ResponseEntity.badRequest().body(Map.of(
                "error", "Bad Request",
                "message", e.getMessage()
        ));
    }
}