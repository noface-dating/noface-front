package com.duri.durifront.face_preference.service;

import com.duri.durifront.face_preference.dto.FacePreferenceAnswerDto;
import com.duri.durifront.face_preference.dto.FacePreferenceResultDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * {@link FacePreferenceService}의 로컬 구현체.
 * <p>기존 서비스 클래스들에 위임하여 질문 로딩, 응답 분석, 결과 저장을 처리한다.
 * core 서버 분리 시 FeignClient 구현체로 교체된다.</p>
 */
@Service
@RequiredArgsConstructor
public class FacePreferenceServiceImpl implements FacePreferenceService {

    private final QuestionLoaderService questionLoaderService;
    private final DescriptionService descriptionService;
    private final FacePreferencePageService pageService;

    @Override
    public List<Map<String, Object>> getQuestionsByGender(String gender) {
        return questionLoaderService.getQuestionsByGender(gender);
    }

    @Override
    public FacePreferenceResultDto processAnswers(List<FacePreferenceAnswerDto> answers, String gender) {
        String key = descriptionService.buildKey(answers, gender);
        String description = descriptionService.getDescription(key, gender);
        int skippedCount = descriptionService.countSkipped(answers);

        return FacePreferenceResultDto.builder()
                .descriptionKey(key)
                .descriptionText(description)
                .skippedCount(skippedCount)
                .answers(answers)
                .build();
    }

    @Override
    public FacePreferenceResultDto getSavedResult(Long userId) {
        FacePreferencePageService.SavedResultDto saved = pageService.getSavedResult(userId);
        return FacePreferenceResultDto.builder()
                .descriptionText(saved.getDescription())
                .skippedCount(0)
                .answers(saved.getAnswers())
                .build();
    }

    @Override
    public void saveResult(Long userId, List<FacePreferenceAnswerDto> answers,
                           String gender, String key, String description) {
        pageService.saveResult(userId, answers, gender, key, description);
    }
}
