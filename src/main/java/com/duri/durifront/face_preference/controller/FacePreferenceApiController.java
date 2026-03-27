package com.duri.durifront.face_preference.controller;

import com.duri.durifront.face_preference.service.QuestionLoaderService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/onboarding/face-preference")
@RequiredArgsConstructor
public class FacePreferenceApiController {

    private final QuestionLoaderService questionLoaderService;

    @GetMapping(value = "/questions", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<Map<String, Object>> questions(
            @RequestParam(defaultValue = "FEMALE") String gender) {
        return questionLoaderService.getQuestionsByGender(gender);
    }
}
