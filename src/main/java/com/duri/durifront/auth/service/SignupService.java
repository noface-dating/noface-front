package com.duri.durifront.auth.service;

import com.duri.durifront.auth.dto.SignupTempData;
import com.duri.durifront.profile.service.ProfileService;
import com.duri.durifront.user.entity.User;
import com.duri.durifront.user.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
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

    public boolean isUsernameDuplicated(String username) {
        return userService.isUsernameDuplicated(username);
    }

    @Transactional
    public void registerUserAndProfile(String tempKey,
                                       String facePreference,
                                       String faceFeatures,
                                       Byte absoluteScore)
    {
        SignupTempData data = signupTempStorageService.getTempData(tempKey);

        if (Objects.isNull(data)) {
            throw new IllegalArgumentException("임시 회원가입 데이터가 존재하지 않습니다.");
        }

        // 1. User 생성
        User user = userService.createUser(
                data.getUsername(),
                data.getPassword(),
                data.getEmail()
        );

        try {
            // 2. JSON --> String 변환
            String hobbiesJson = Objects.isNull(data.getHobbies())
                    ? "[]"  // TODO: 현재 - 취미 정보는 빈 배열로 우선 생성
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
                    faceFeatures,
                    facePreference,
                    additionalInformationJson,
                    absoluteScore
            );
        } catch (Exception e) {
            throw new RuntimeException("Profile JSON 변환 중 오류 발생", e);
        }
    }
}
