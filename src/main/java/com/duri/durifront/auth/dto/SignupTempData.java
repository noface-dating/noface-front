package com.duri.durifront.auth.dto;

import java.time.LocalDate;
import java.util.List;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SignupTempData {

    // Signup 입력받은 정보
    private String username;
    private String password;
    private String email;

    // Signup Profile 입력받은 정보
    private String nickname;
    private LocalDate birthDate;
    private Boolean gender;
    private String region;
    private List<String> additionalInformation;
    private List<String> hobbies;

}
