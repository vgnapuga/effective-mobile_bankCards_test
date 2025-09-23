package com.example.bankcards.controller.advice;


import java.nio.file.AccessDeniedException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

import com.example.bankcards.dto.error.DTOValidationErrorResponse;
import com.example.bankcards.dto.error.ErrorResponse;
import com.example.bankcards.exception.BusinessRuleViolationException;
import com.example.bankcards.exception.CardEncryptionException;
import com.example.bankcards.exception.DomainValidationException;
import com.example.bankcards.exception.ResourceAlreadyExistsException;
import com.example.bankcards.exception.ResourceNotFoundException;


@RestControllerAdvice
public final class GlobalExceptionHandler {

    private static String getPath(final WebRequest request) {
        return request.getDescription(false).replace("uri=", "");
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<DTOValidationErrorResponse> handleDTOValidationExceptions(
            final MethodArgumentNotValidException exception,
            final WebRequest request) {
        Map<String, List<String>> errorFields = new HashMap<>();

        exception.getBindingResult().getFieldErrors().forEach(error -> {
            String fieldName = error.getField();
            String errorMessage = error.getDefaultMessage();

            errorFields.computeIfAbsent(fieldName, k -> new ArrayList<>()).add(errorMessage);
        });

        DTOValidationErrorResponse response = DTOValidationErrorResponse.of(
                exception.getMessage(),
                getPath(request),
                HttpStatus.BAD_REQUEST.value(),
                errorFields);

        return ResponseEntity.badRequest().body(response);
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ErrorResponse> handleAccessDeniedExceptions(
            final AccessDeniedException exception,
            final WebRequest request) {
        ErrorResponse response = ErrorResponse.of(
                "ACCESS_PERMISSION_ERROR",
                exception.getMessage(),
                getPath(request),
                HttpStatus.FORBIDDEN.value());

        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);
    }

    @ExceptionHandler(BusinessRuleViolationException.class)
    public ResponseEntity<ErrorResponse> handleBusinessRuleViolationExceptions(
            final BusinessRuleViolationException exception,
            final WebRequest request) {
        ErrorResponse response = ErrorResponse.of(
                "BUSINESS_RULE_VIOLATION",
                exception.getMessage(),
                getPath(request),
                HttpStatus.BAD_REQUEST.value());

        return ResponseEntity.badRequest().body(response);
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleResourceNotFoundExceptions(
            final ResourceNotFoundException exception,
            final WebRequest request) {
        ErrorResponse response = ErrorResponse.of(
                "RESOURCE_NOT_FOUND",
                exception.getMessage(),
                getPath(request),
                HttpStatus.NOT_FOUND.value());

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
    }

    @ExceptionHandler(ResourceAlreadyExistsException.class)
    public ResponseEntity<ErrorResponse> handleResourceAlreadyExistsExceptions(
            final ResourceAlreadyExistsException exception,
            final WebRequest request) {
        ErrorResponse response = ErrorResponse.of(
                "RESOURCE_ALREADY_EXISTS",
                exception.getMessage(),
                getPath(request),
                HttpStatus.CONFLICT.value());

        return ResponseEntity.status(HttpStatus.CONFLICT.value()).body(response);
    }

    @ExceptionHandler(CardEncryptionException.class)
    public ResponseEntity<ErrorResponse> handleCardEncryptionException(
            final CardEncryptionException exception,
            final WebRequest request) {
        ErrorResponse response = ErrorResponse.of(
                "CARD_ENCRYPTION_ERROR",
                exception.getMessage(),
                getPath(request),
                HttpStatus.INTERNAL_SERVER_ERROR.value());

        return ResponseEntity.internalServerError().body(response);
    }

    @ExceptionHandler(DomainValidationException.class)
    public ResponseEntity<ErrorResponse> handleDomainValidationExceptions(
            final DomainValidationException exception,
            final WebRequest request) {
        ErrorResponse response = ErrorResponse.of(
                "DOMAIN_VALIDATION_ERROR",
                "Card processing failed",
                getPath(request),
                HttpStatus.BAD_REQUEST.value());

        return ResponseEntity.badRequest().body(response);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGenericExceptions(final Exception exception, final WebRequest request) {
        ErrorResponse response = ErrorResponse.of(
                "INTERNAL_SERVER_ERROR",
                "An unexpected error occurred",
                getPath(request),
                HttpStatus.INTERNAL_SERVER_ERROR.value());

        return ResponseEntity.internalServerError().body(response);
    }

}
