package com.duri.durifront.face_preference.controller;

import com.duri.durifront.face_preference.dto.FacePreferenceAnswerDto;
import com.duri.durifront.face_preference.service.DescriptionService;
import com.duri.durifront.face_preference.service.FacePreferencePageService;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.duri.durifront.face_preference.dto.FacePreferenceSessionDto;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/face-preference")
@RequiredArgsConstructor
public class FacePreferenceController {

    private final FacePreferencePageService pageService;
    private final DescriptionService descriptionService;
    private final ObjectMapper objectMapper;

    @GetMapping("/find-type")
    public String findType() {
        return "face_preference/find-type";
    }

    @GetMapping("/quiz")
    public String quiz(@RequestParam(defaultValue = "FEMALE") String gender, Model model) {
        model.addAttribute("gender", gender);
        return "face_preference/quiz";
    }

    public static final String SESSION_KEY = "facePreferenceResult";

    @PostMapping("/submit")
    public String submit(
            @RequestParam(defaultValue = "1") Long userId,
            @RequestParam String gender,
            @RequestParam String answersJson,
            HttpSession session,
            Model model) {
        try {
            List<FacePreferenceAnswerDto> answers = objectMapper.readValue(
                    answersJson, new TypeReference<>() {});

            String key = descriptionService.buildKey(answers, gender);
            String description = descriptionService.getDescription(key, gender);
            int skippedCount = descriptionService.countSkipped(answers);

            session.setAttribute(SESSION_KEY,
                    new FacePreferenceSessionDto(key, description, gender, answers));

            try {
                pageService.saveResult(userId, answers, gender, key, description);
            } catch (Exception ignored) {}

            model.addAttribute("description", description);
            model.addAttribute("answers", answers);
            model.addAttribute("skippedCount", skippedCount);
            model.addAttribute("gender", gender);
        } catch (Exception e) {
            model.addAttribute("description", "취향 분석 완료! 당신만의 이상형이 있군요. 🎯");
            model.addAttribute("skippedCount", 0);
            model.addAttribute("answers", List.of());
        }
        return "face_preference/result";
    }

    @GetMapping("/result")
    public String result(@RequestParam(defaultValue = "1") Long userId, Model model) {
        try {
            var saved = pageService.getSavedResult(userId);
            model.addAttribute("description", saved.getDescription());
            model.addAttribute("answers", saved.getAnswers());
            model.addAttribute("skippedCount", 0);
        } catch (Exception e) {
            model.addAttribute("description", "");
            model.addAttribute("answers", List.of());
            model.addAttribute("skippedCount", 0);
        }
        return "face_preference/result";
    }
}
