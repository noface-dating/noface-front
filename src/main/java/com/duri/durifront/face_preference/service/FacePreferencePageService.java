package com.duri.durifront.face_preference.service;

import com.duri.durifront.face_preference.dto.FacePreferenceAnswerDto;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class FacePreferencePageService {

    public void saveResult(Long userId, List<FacePreferenceAnswerDto> answers,
                           String gender, String key, String description) {
        // 세션에 저장되므로 별도 DB 저장 불필요 (회원가입 완료 시 처리)
    }

    public SavedResultDto getSavedResult(Long userId) {
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
