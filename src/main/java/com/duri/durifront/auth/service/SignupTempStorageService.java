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

    public void saveStep01(String tempKey, Boolean gender) {
        SignupTempData data = signupTempDataMap.getOrDefault(tempKey, new SignupTempData());
        data.setGender(gender);
        signupTempDataMap.put(tempKey, data);
    }

    public void saveStep02(String tempKey, String facePreference) {
        SignupTempData data = signupTempDataMap.get(tempKey);
        data.setFacePreference(facePreference);
    }

    public void saveStep03(String tempKey, String username, String password, String email) {
        SignupTempData data = signupTempDataMap.get(tempKey);
        data.setUsername(username);
        data.setPassword(password);
        data.setEmail(email);
    }

    public void saveStep04(String tempKey, String nickname, LocalDate birthDate,
                           String region, List<String> additionalInformation, List<String> hobbies) {
        SignupTempData data = signupTempDataMap.get(tempKey);
        data.setNickname(nickname);
        data.setBirthDate(birthDate);
        data.setRegion(region);
        data.setAdditionalInformation(additionalInformation);
        data.setHobbies(hobbies);
    }

    public void saveStep05(String tempKey, String faceFeatures, Byte absoluteScore) {
        SignupTempData data = signupTempDataMap.get(tempKey);
        data.setFaceFeatures(faceFeatures);
        data.setAbsoluteScore(absoluteScore);
    }

    public SignupTempData getTempData(String tempKey) {
        return signupTempDataMap.get(tempKey);
    }

    public void removeTempData(String tempKey) {
        signupTempDataMap.remove(tempKey);
    }
}
