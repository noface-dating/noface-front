package com.duri.durifront.face_preference.service;

import com.duri.durifront.face_preference.dto.FacePreferenceAnswerDto;
import com.duri.durifront.face_preference.dto.FacePreferenceResultDto;

import java.util.List;
import java.util.Map;

/**
 * 취향 이지선다 비즈니스 로직 인터페이스.
 * <p>질문 조회, 응답 처리, 결과 조회 기능을 정의하며,
 * 구현체 교체를 통해 core 서버 연동이 가능하다.</p>
 */
public interface FacePreferenceService {

    /**
     * 성별에 해당하는 이지선다 질문 목록을 반환한다.
     *
     * @param gender 대상 성별 (MALE / FEMALE)
     * @return 질문 목록
     */
    List<Map<String, Object>> getQuestionsByGender(String gender);

    /**
     * 응답 목록을 분석하여 이상형 결과를 생성한다.
     *
     * @param answers 질문별 응답 목록
     * @param gender  선택 대상 성별
     * @return 이상형 분석 결과 DTO
     */
    FacePreferenceResultDto processAnswers(List<FacePreferenceAnswerDto> answers, String gender);

    /**
     * 저장된 이지선다 결과를 조회한다.
     *
     * @param userId 사용자 ID
     * @return 저장된 결과 DTO
     */
    FacePreferenceResultDto getSavedResult(Long userId);

    /**
     * 이지선다 결과를 저장한다.
     *
     * @param userId      사용자 ID
     * @param answers     질문별 응답 목록
     * @param gender      선택 대상 성별
     * @param key         이상형 설명 키
     * @param description 이상형 설명 텍스트
     */
    void saveResult(Long userId, List<FacePreferenceAnswerDto> answers,
                    String gender, String key, String description);
}
