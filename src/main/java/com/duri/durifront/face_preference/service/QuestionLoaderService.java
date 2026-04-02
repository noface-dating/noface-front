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

@Service
public class QuestionLoaderService {

    private final ObjectMapper objectMapper = new ObjectMapper();
    private List<Map<String, Object>> allQuestions;

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
