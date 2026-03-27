package com.duri.durifront.face_preference.service;

import com.duri.durifront.entity.Profile;
import com.duri.durifront.face_preference.dto.FacePreferenceAnswerDto;
import com.duri.durifront.repository.ProfileRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class FacePreferencePageService {

    private final ProfileRepository profileRepository;

    @Transactional
    public void saveResult(Long userId, List<FacePreferenceAnswerDto> answers,
                           String gender, String key, String description) {
        Profile profile = profileRepository.findByUserUserId(userId)
                .orElseThrow(() -> new RuntimeException("Profile not found"));
        List<Integer> prefArray = buildPrefArray(answers, gender);
        profile.setFacePreference(prefArray);
        profileRepository.save(profile);
    }

    @Transactional(readOnly = true)
    public SavedResultDto getSavedResult(Long userId) {
        Profile profile = profileRepository.findByUserUserId(userId)
                .orElseThrow(() -> new RuntimeException("Profile not found"));
        return SavedResultDto.builder()
                .description("")
                .answers(List.of())
                .build();
    }

    List<Integer> buildPrefArray(List<FacePreferenceAnswerDto> answers, String gender) {
        String[] order = {"q1", "q2", "q3", "q4", "q5", "q6", "q7", "q8", "q9", "q10"};
        List<Integer> result = new ArrayList<>();
        for (String qid : order) {
            FacePreferenceAnswerDto a = answers.stream()
                    .filter(x -> qid.equals(x.getQuestionId())).findFirst().orElse(null);
            if (a == null || a.getChoiceId() == null || Boolean.TRUE.equals(a.getIsTimeout())) {
                result.add(0);
            } else {
                result.add(a.getChoiceId().endsWith("-1") ? 0 : 1);
            }
        }
        return result;
    }

    @lombok.Data
    @lombok.Builder
    @lombok.NoArgsConstructor
    @lombok.AllArgsConstructor
    public static class SavedResultDto {
        private String description;
        private List<FacePreferenceAnswerDto> answers;
    }
}
