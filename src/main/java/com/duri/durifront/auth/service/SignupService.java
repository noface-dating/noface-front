package com.duri.durifront.auth.service;

import com.duri.durifront.auth.dto.SignupTempData;
import com.duri.durifront.profile.service.ProfileService;
import com.duri.durifront.user.entity.User;
import com.duri.durifront.user.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.LocalDate;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class SignupService {

    private final ObjectMapper objectMapper;

    private final UserService userService;
    private final ProfileService profileService;
    private final SignupTempStorageService signupTempStorageService;

    @Transactional
    public void registerUserAndProfile(String tempKey) {
        SignupTempData data = signupTempStorageService.getTempData(tempKey);

        // 1. User 생성
        User user = userService.createUser(
                data.getUsername(),
                data.getPassword(),
                data.getEmail()
        );

        try {
            // 2. JSON --> String 변환
            String hobbiesJson = Objects.isNull(data.getHobbies())
                    ? "[]"
                    : objectMapper.writeValueAsString(data.getHobbies());

            String additionalInformationJson = Objects.isNull(data.getAdditionalInformation())
                    ? "[]"
                    : objectMapper.writeValueAsString(data.getAdditionalInformation());

            // 3. Profile 생성
            profileService.createProfile(
                    user,
                    data.getNickname(),
                    data.getBirthDate(),
                    data.getGender(),
                    data.getRegion(),
                    hobbiesJson,
                    data.getFaceFeatures(),
                    data.getFacePreference(),
                    additionalInformationJson,
                    data.getAbsoluteScore()
            );
        } catch (Exception e) {
            throw new RuntimeException("Profile JSON 변환 중 오류 발생", e);
        }
    }
}
