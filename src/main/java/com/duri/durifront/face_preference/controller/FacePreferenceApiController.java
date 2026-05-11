package com.duri.durifront.face_preference.controller;

import com.duri.durifront.face_preference.service.FacePreferenceService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

/**
 * 취향 이지선다 질문 조회 REST API 컨트롤러.
 * <p>온보딩 과정에서 성별에 따른 이지선다 질문 목록을 JSON으로 제공한다.</p>
 */
@Tag(name = "Face Preference", description = "취향 이지선다 API")
@RestController
@RequestMapping("/api/onboarding/face-preference")
@RequiredArgsConstructor
public class FacePreferenceApiController {

    private final FacePreferenceService facePreferenceService;

    /**
     * 성별에 해당하는 이지선다 질문 목록을 반환한다.
     *
     * @param gender 대상 성별 (MALE / FEMALE)
     * @return 질문 목록 (질문 ID, 질문 텍스트, 선택지 이미지 등 포함)
     */
    @Operation(
            summary = "이지선다 질문 목록 조회",
            description = "성별에 따른 취향 이지선다 질문과 선택지 목록을 반환한다."
    )
    @ApiResponse(responseCode = "200", description = "질문 목록 반환 성공")
    @GetMapping(value = "/questions", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<Map<String, Object>> questions(
            @Parameter(description = "대상 성별 (MALE / FEMALE)", example = "FEMALE")
            @RequestParam(defaultValue = "FEMALE") String gender) {
        return facePreferenceService.getQuestionsByGender(gender);
    }
}
