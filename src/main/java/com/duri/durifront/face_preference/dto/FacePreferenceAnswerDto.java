package com.duri.durifront.face_preference.dto;

import lombok.*;

import java.io.Serializable;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FacePreferenceAnswerDto implements Serializable {
    private String questionId;
    private String questionText;
    private String choiceId;
    private String choiceLabel;
    private Boolean isTimeout;
    private String answeredAt;
}
