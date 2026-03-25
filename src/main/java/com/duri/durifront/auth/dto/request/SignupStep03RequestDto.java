package com.duri.durifront.auth.dto.request;

// TODO: Validation Annotations 추가
public record SignupStep03RequestDto(
        String username,
        String password,
        String email
) {
}
