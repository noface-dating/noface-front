package com.duri.durifront.auth.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.LocalDate;
import java.util.List;

// TODO: Validation Annotations 추가
public record SignupStep04RequestDto(
        String nickname,
        LocalDate birthDate,
        String region,
        List<String> additionalInformation,
        List<String> hobbies
) {
}
