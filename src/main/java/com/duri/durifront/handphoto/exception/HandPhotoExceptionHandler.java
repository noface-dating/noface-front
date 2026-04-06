package com.duri.durifront.handphoto.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class HandPhotoExceptionHandler {

    @ExceptionHandler(HandPhotoException.class)
    public ResponseEntity<ErrorResponse> handleHandPhotoException(HandPhotoException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .contentType(MediaType.APPLICATION_JSON)
                .body(new ErrorResponse("HAND_PHOTO_ERROR", ex.getMessage()));
    }

    public record ErrorResponse(String code, String message) {}
}
