package com.duri.durifront.auth.dto.request;

import java.time.LocalDate;
import java.util.List;

// TODO: Validation Annotations 추가
public record SignupProfileRequestDto(
        String nickname,
        LocalDate birthDate,
        Boolean gender,
        String region,
        List<String> additionalInformation,
        List<String> hobbies
) {
}
