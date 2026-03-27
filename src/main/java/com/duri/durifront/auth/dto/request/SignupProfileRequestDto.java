package com.duri.durifront.auth.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Size;
import java.time.LocalDate;
import java.util.List;

public record SignupProfileRequestDto(
        @NotBlank(message = "닉네임은 필수입니다.")
        @Size(max = 50, message = "닉네임은 50자 이하로 입력해주세요.")
        String nickname,

        @NotNull(message = "생년월일은 필수입니다.")
        @Past(message = "생년월일은 과거 날짜여야 합니다.")
        LocalDate birthDate,

        @NotNull(message = "성별은 필수입니다.")
        Boolean gender,

        @Size(max = 50, message = "지역은 50자 이하로 입력해주세요.")
        String region,

        List<String> additionalInformation,

        List<String> hobbies
) {
}
