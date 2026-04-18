package com.duri.durifront.auth.dto.response;


import com.duri.durifront.auth.exception.BaseErrorCode;

public record ErrorResponseDto(
        String code,
        String message,
        // TODO 제거 필요한 DEBUG POINT
        String trace
) {

    // TODO 제거 필요한 DEBUG POINT
    public static ErrorResponseDto from (BaseErrorCode errorCode, Exception e) {
        return new ErrorResponseDto(
                errorCode.getCode(),
                errorCode.getMessage(),
                e.getMessage()
        );
    }
}
