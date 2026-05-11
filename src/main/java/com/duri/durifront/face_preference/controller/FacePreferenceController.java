package com.duri.durifront.face_preference.controller;

import com.duri.durifront.face_preference.dto.FacePreferenceAnswerDto;
import com.duri.durifront.face_preference.dto.FacePreferenceResultDto;
import com.duri.durifront.face_preference.dto.FacePreferenceSessionDto;
import com.duri.durifront.face_preference.service.FacePreferenceService;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 취향 이지선다 화면 컨트롤러.
 * <p>이지선다 퀴즈 진행, 결과 제출 및 결과 조회 등의 페이지를 처리한다.</p>
 */
@Controller
@RequestMapping("/face-preference")
@RequiredArgsConstructor
public class FacePreferenceController {

    private final FacePreferenceService facePreferenceService;
    private final ObjectMapper objectMapper;

    public static final String SESSION_KEY = "facePreferenceResult";

    /**
     * 이상형 유형 찾기 안내 페이지를 반환한다.
     *
     * @return 유형 찾기 뷰 이름
     */
    @GetMapping("/find-type")
    public String findType() {
        return "face_preference/find-type";
    }

    /**
     * 이지선다 퀴즈 화면을 반환한다.
     *
     * @param gender 선택 대상 성별 (MALE / FEMALE)
     * @param model  뷰에 전달할 모델
     * @return 퀴즈 뷰 이름
     */
    @GetMapping("/quiz")
    public String quiz(@RequestParam(defaultValue = "FEMALE") String gender, Model model) {
        model.addAttribute("gender", gender);
        return "face_preference/quiz";
    }

    /**
     * 이지선다 퀴즈 응답을 제출하고 결과 화면을 반환한다.
     * <p>응답을 분석하여 이상형 설명 키와 텍스트를 생성한 뒤 세션에 저장한다.</p>
     *
     * @param userId      사용자 ID
     * @param gender      선택 대상 성별
     * @param answersJson 질문별 응답을 담은 JSON 문자열
     * @param session     결과 저장용 HTTP 세션
     * @param model       뷰에 전달할 모델
     * @return 결과 뷰 이름
     */
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

            FacePreferenceResultDto result = facePreferenceService.processAnswers(answers, gender);

            session.setAttribute(SESSION_KEY,
                    new FacePreferenceSessionDto(
                            result.getDescriptionKey(),
                            result.getDescriptionText(),
                            gender,
                            answers));

            try {
                facePreferenceService.saveResult(
                        userId, answers, gender,
                        result.getDescriptionKey(), result.getDescriptionText());
            } catch (Exception ignored) {}

            model.addAttribute("description", result.getDescriptionText());
            model.addAttribute("answers", result.getAnswers());
            model.addAttribute("skippedCount", result.getSkippedCount());
            model.addAttribute("gender", gender);
        } catch (Exception e) {
            model.addAttribute("description", "취향 분석 완료! 당신만의 이상형이 있군요. 🎯");
            model.addAttribute("skippedCount", 0);
            model.addAttribute("answers", List.of());
        }
        return "face_preference/result";
    }

    /**
     * 기존에 저장된 이지선다 결과를 조회하여 결과 화면을 반환한다.
     *
     * @param userId 사용자 ID
     * @param model  뷰에 전달할 모델
     * @return 결과 뷰 이름
     */
    @GetMapping("/result")
    public String result(@RequestParam(defaultValue = "1") Long userId, Model model) {
        try {
            FacePreferenceResultDto saved = facePreferenceService.getSavedResult(userId);
            model.addAttribute("description", saved.getDescriptionText());
            model.addAttribute("answers", saved.getAnswers());
            model.addAttribute("skippedCount", saved.getSkippedCount());
        } catch (Exception e) {
            model.addAttribute("description", "");
            model.addAttribute("answers", List.of());
            model.addAttribute("skippedCount", 0);
        }
        return "face_preference/result";
    }
}
