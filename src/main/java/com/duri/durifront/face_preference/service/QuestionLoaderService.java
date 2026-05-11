package com.duri.durifront.face_preference.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PostConstruct;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import java.io.InputStream;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 취향 이지선다 질문 로딩 서비스.
 * <p>애플리케이션 시작 시 {@code questions.json}을 읽어 메모리에 캐싱하고,
 * 성별에 따라 필터링된 질문 목록을 제공한다.</p>
 */
@Service
public class QuestionLoaderService {

    private final ObjectMapper objectMapper = new ObjectMapper();
    private List<Map<String, Object>> allQuestions;

    /**
     * 애플리케이션 시작 시 {@code classpath:questions.json}에서 전체 질문 목록을 로딩한다.
     */
    @PostConstruct
    public void init() {
        try {
            ClassPathResource resource = new ClassPathResource("questions.json");
            try (InputStream is = resource.getInputStream()) {
                allQuestions = objectMapper.readValue(is, new TypeReference<>() {});
            }
        } catch (Exception e) {
            allQuestions = List.of();
        }
    }

    /**
     * 지정 성별에 해당하는 질문 목록을 질문 ID 순으로 정렬하여 반환한다.
     *
     * @param gender 대상 성별 (MALE / FEMALE)
     * @return 해당 성별의 질문 목록, 데이터가 없으면 빈 리스트
     */
    @SuppressWarnings("unchecked")
    public List<Map<String, Object>> getQuestionsByGender(String gender) {
        if (allQuestions == null) return List.of();
        return allQuestions.stream()
                .filter(q -> gender.equalsIgnoreCase(String.valueOf(q.get("gender"))))
                .sorted(Comparator.comparing(q -> {
                    String id = (String) q.get("questionId");
                    return id == null ? "" : id.replace("q", "");
                }, Comparator.comparingInt(s -> {
                    try { return Integer.parseInt(s); } catch (NumberFormatException e) { return 0; }
                })))
                .collect(Collectors.toList());
    }
}
