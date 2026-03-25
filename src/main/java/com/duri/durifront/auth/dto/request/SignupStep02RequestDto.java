package com.duri.durifront.auth.dto.request;

// TODO: Validation Annotations 추가
public record SignupStep02RequestDto(
        // 10자리 이진 문자열
        String facePreference
) {
}
