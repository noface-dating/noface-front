package com.duri.durifront.face_preference.service;

import com.duri.durifront.face_preference.dto.FacePreferenceAnswerDto;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * 취향 이지선다 결과 저장 및 조회 서비스.
 * <p>퀴즈 응답 결과를 세션 기반으로 관리하며, 응답 배열을 정수 배열로 변환하는 기능을 제공한다.</p>
 */
@Service
public class FacePreferencePageService {

    /**
     * 이지선다 결과를 저장한다.
     * <p>현재는 세션에 저장되므로 별도 DB 저장은 수행하지 않으며, 회원가입 완료 시 처리된다.</p>
     *
     * @param userId      사용자 ID
     * @param answers     질문별 응답 목록
     * @param gender      선택 대상 성별
     * @param key         이상형 설명 키
     * @param description 이상형 설명 텍스트
     */
    public void saveResult(Long userId, List<FacePreferenceAnswerDto> answers,
                           String gender, String key, String description) {
        // 세션에 저장되므로 별도 DB 저장 불필요 (회원가입 완료 시 처리)
    }

    /**
     * 저장된 이지선다 결과를 조회한다.
     *
     * @param userId 사용자 ID
     * @return 저장된 결과 DTO (설명 텍스트 + 응답 목록)
     */
    public SavedResultDto getSavedResult(Long userId) {
        return SavedResultDto.builder()
                .description("")
                .answers(List.of())
                .build();
    }

    /**
     * 응답 목록을 정수 배열(0 또는 1)로 변환한다.
     * <p>q1~q10 순서로 정렬하며, 타임아웃이거나 미응답인 경우 0, 두 번째 선택지를 고른 경우 1로 매핑한다.</p>
     *
     * @param answers 질문별 응답 목록
     * @param gender  선택 대상 성별
     * @return 10개 원소의 정수 리스트
     */
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

    /**
     * 저장된 이지선다 결과를 담는 내부 DTO.
     */
    @lombok.Data
    @lombok.Builder
    @lombok.NoArgsConstructor
    @lombok.AllArgsConstructor
    public static class SavedResultDto {
        /** 이상형 설명 텍스트 */
        private String description;
        /** 질문별 응답 목록 */
        private List<FacePreferenceAnswerDto> answers;
    }
}
