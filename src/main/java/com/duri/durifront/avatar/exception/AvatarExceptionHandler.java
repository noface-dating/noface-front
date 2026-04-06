package com.duri.durifront.avatar.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class AvatarExceptionHandler {

    @ExceptionHandler(AvatarException.class)
    public ResponseEntity<ErrorResponse> handleAvatarException(AvatarException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .contentType(MediaType.APPLICATION_JSON)
                .body(new ErrorResponse("AVATAR_ERROR", ex.getMessage()));
    }

    public record ErrorResponse(String code, String message) {}
}
