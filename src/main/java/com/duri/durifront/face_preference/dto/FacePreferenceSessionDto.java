package com.duri.durifront.face_preference.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.io.Serializable;
import java.util.List;

/**
 * 취향 이지선다 결과를 세션에 저장하기 위한 DTO.
 * <p>퀴즈 완료 후 회원가입 시점까지 세션에 보관된다.</p>
 */
@Schema(description = "이지선다 결과 세션 저장용 DTO")
@Getter
@AllArgsConstructor
public class FacePreferenceSessionDto implements Serializable {

    @Schema(description = "이상형 설명 키 (10자리 이진 문자열)", example = "0010110100")
    private final String descriptionKey;

    @Schema(description = "이상형 설명 텍스트")
    private final String descriptionText;

    @Schema(description = "선택 대상 성별", example = "FEMALE")
    private final String targetGender;

    @Schema(description = "질문별 응답 목록")
    private final List<FacePreferenceAnswerDto> answers;
}
