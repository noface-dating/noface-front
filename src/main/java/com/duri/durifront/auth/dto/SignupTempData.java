package com.duri.durifront.auth.dto;

import java.time.LocalDate;
import java.util.List;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SignupTempData {
    // Step01
    private Boolean gender;

    // Step02
    private String facePreference;

    // Step03
    private String username;
    private String password;
    private String email;

    // Step04
    private String nickname;
    private LocalDate birthDate;
    private String region;
    private List<String> additionalInformation;
    private List<String> hobbies;

    // Step05
    private String faceFeatures;
    private Byte absoluteScore;
}
