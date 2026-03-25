package com.duri.durifront.auth.dto.request;

// TODO: Validation Annotations 추가
public record SignupStep01RequestDto(
        // true: 남성
        // false: 여성
        Boolean gender
) {
}
