package com.duri.durifront.face_preference.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.io.Serializable;

/**
 * 취향 이지선다 개별 질문에 대한 응답 DTO.
 */
@Schema(description = "이지선다 개별 질문 응답")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FacePreferenceAnswerDto implements Serializable {

    @Schema(description = "질문 ID", example = "q1")
    private String questionId;

    @Schema(description = "질문 텍스트", example = "어떤 눈이 더 끌려?")
    private String questionText;

    @Schema(description = "선택한 선택지 ID", example = "q1-1")
    private String choiceId;

    @Schema(description = "선택한 선택지 라벨", example = "강아지상")
    private String choiceLabel;

    @Schema(description = "타임아웃 여부 (시간 초과 시 true)", example = "false")
    private Boolean isTimeout;

    @Schema(description = "응답 시각 (ISO 8601)", example = "2026-05-05T21:00:00")
    private String answeredAt;
}
