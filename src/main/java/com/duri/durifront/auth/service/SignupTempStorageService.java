package com.duri.durifront.auth.service;

import com.duri.durifront.auth.dto.SignupTempData;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class SignupTempStorageService {

    private final Map<String, SignupTempData> signupTempDataMap = new ConcurrentHashMap<>();

    public void saveSignupUser(String tempKey,
                               String username,
                               String password,
                               String email)
    {
        SignupTempData data = signupTempDataMap.computeIfAbsent(tempKey, k -> new SignupTempData());
        data.setUsername(username);
        data.setPassword(password);
        data.setEmail(email);
    }

    public void saveSignupProfile(String tempKey,
                                  String nickname,
                                  LocalDate birthDate,
                                  Boolean gender,
                                  String region,
                                  List<String> additionalInformation,
                                  List<String> hobbies)
    {
        SignupTempData data = signupTempDataMap.get(tempKey);
        data.setNickname(nickname);
        data.setBirthDate(birthDate);
        data.setGender(gender);
        data.setRegion(region);
        data.setAdditionalInformation(additionalInformation);
        data.setHobbies(hobbies);
    }

    public SignupTempData getTempData(String tempKey) {
        return signupTempDataMap.get(tempKey);
    }

    public void removeTempData(String tempKey) {
        signupTempDataMap.remove(tempKey);
    }
}
