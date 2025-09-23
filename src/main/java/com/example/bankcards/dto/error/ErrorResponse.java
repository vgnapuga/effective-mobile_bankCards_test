package com.example.bankcards.dto.error;


import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonFormat;


public record ErrorResponse(
        String error,
        String message,
        String path,
        @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime timeStamp,
        int status) {

    public static ErrorResponse of(final String error, final String message, final String path, final int status) {
        return new ErrorResponse(error, message, path, LocalDateTime.now(), status);
    }

    public static ErrorResponse of(final String error, final String message, final int status) {
        return new ErrorResponse(error, message, null, LocalDateTime.now(), status);
    }

}
