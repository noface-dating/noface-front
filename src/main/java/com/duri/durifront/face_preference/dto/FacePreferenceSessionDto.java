package com.duri.durifront.face_preference.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.io.Serializable;
import java.util.List;

@Getter
@AllArgsConstructor
public class FacePreferenceSessionDto implements Serializable {

    private final String descriptionKey;
    private final String descriptionText;
    private final String targetGender;
    private final List<FacePreferenceAnswerDto> answers;
}
