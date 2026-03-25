package com.duri.durifront.auth.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;

// TODO: Validation Annotations 추가
public record SignupStep05RequestDto(
        String faceFeatures,
        Byte absoluteScore
) {
}
