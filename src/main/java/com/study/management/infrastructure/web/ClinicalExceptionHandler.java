package com.study.management.infrastructure.web;

import com.study.management.domain.model.exception.BusinessRuleException;
import com.study.management.domain.model.exception.ResourceNotFoundException;
import com.study.management.infrastructure.web.dto.ErrorResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

/**
 * Handles clinical domain exceptions and maps them to HTTP responses.
 */
@RestControllerAdvice
public class ClinicalExceptionHandler {

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleNotFound(ResourceNotFoundException ex, WebRequest req) {
        String path = req.getDescription(false).replace("uri=", "");
        ErrorResponse err = new ErrorResponse("NOT_FOUND", ex.getMessage(), path);
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(err);
    }

    @ExceptionHandler(BusinessRuleException.class)
    public ResponseEntity<ErrorResponse> handleBusinessRule(BusinessRuleException ex, WebRequest req) {
        String path = req.getDescription(false).replace("uri=", "");
        ErrorResponse err = new ErrorResponse("BUSINESS_RULE_VIOLATION", ex.getMessage(), path);
        return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).body(err);
    }
}
