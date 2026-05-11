package com.duri.durifront.auth.dto.response;


import com.duri.durifront.auth.exception.BaseErrorCode;

public record ErrorResponseDto(
        String code,
        String message
) {
    public static ErrorResponseDto from (BaseErrorCode errorCode) {
        return new ErrorResponseDto(
                errorCode.getCode(),
                errorCode.getMessage()
        );
    }
}
