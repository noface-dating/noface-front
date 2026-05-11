package com.duri.durifront.face_preference.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.io.Serializable;
import java.util.List;

/**
 * 취향 이지선다 응답 처리 결과 DTO.
 * <p>서비스에서 응답을 분석한 결과를 컨트롤러에 전달하기 위해 사용한다.</p>
 */
@Schema(description = "이지선다 응답 처리 결과")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FacePreferenceResultDto implements Serializable {

    @Schema(description = "이상형 설명 키 (10자리 이진 문자열)", example = "0010110100")
    private String descriptionKey;

    @Schema(description = "이상형 설명 텍스트")
    private String descriptionText;

    @Schema(description = "건너뛴 질문 수", example = "2")
    private int skippedCount;

    @Schema(description = "질문별 응답 목록")
    private List<FacePreferenceAnswerDto> answers;
}
