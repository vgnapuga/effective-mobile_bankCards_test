package com.example.bankcards.dto.error;


import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonFormat;


public record DTOValidationErrorResponse(
        String error,
        String message,
        String path,
        @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime timeStamp,
        int status,
        Map<String, List<String>> errorFields) {

    private static final String ERROR_NAME = "DTO_VALIDATION_ERROR";

    public static DTOValidationErrorResponse of(
            final String message,
            final String path,
            final int status,
            final Map<String, List<String>> errorFields) {
        return new DTOValidationErrorResponse(ERROR_NAME, message, path, LocalDateTime.now(), status, errorFields);
    }

    public static DTOValidationErrorResponse of(
            final String message,
            final int status,
            final Map<String, List<String>> errorFields) {
        return new DTOValidationErrorResponse(ERROR_NAME, message, null, LocalDateTime.now(), status, errorFields);
    }

}
