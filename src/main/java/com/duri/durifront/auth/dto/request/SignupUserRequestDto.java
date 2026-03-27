package com.duri.durifront.auth.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record SignupUserRequestDto(

        @NotBlank(message = "아이디는 필수입니다.")
        @Size(max = 100, message = "아이디는 100자 이하로 입력해주세요.")
        String username,

        @NotBlank(message = "비밀번호는 필수입니다.")
        @Size(min = 8, message = "비밀번호는 8자 이상 입력해주세요.")
        String password,

        @NotBlank(message = "이메일은 필수입니다.")
        @Size(max = 100, message = "이메일은 100자 이하로 입력해주세요.")
        @Email(message = "올바른 이메일 형식이 아닙니다.")
        String email
) {
}
