package com.duri.durifront.entity;

import com.duri.durifront.converter.FacePreferenceConverter;
import com.duri.durifront.converter.JsonMapConverter;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@Entity
@Table(name = "profile")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Profile {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "profile_id")
    private Long profileId;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;

    @Column(nullable = false, length = 50)
    private String nickname;

    @Column(name = "birth_date", nullable = false)
    private LocalDate birthDate;

    @Column(nullable = false)
    private Boolean gender;

    @Column(length = 50)
    private String region;

    @Convert(converter = JsonMapConverter.class)
    @Column(columnDefinition = "TEXT")
    private Map<String, Object> hobbies;

    @Column(name = "face_features", length = 10)
    @Convert(converter = FacePreferenceConverter.class)
    private List<Integer> faceFeatures;

    @Column(name = "face_preference", length = 10)
    @Convert(converter = FacePreferenceConverter.class)
    private List<Integer> facePreference;

    @Convert(converter = JsonMapConverter.class)
    @Column(name = "additional_info", columnDefinition = "TEXT")
    private Map<String, Object> additionalInfo;

    @Column(name = "absolute_score")
    private Byte absoluteScore;

    @Column(name = "description_key", length = 10)
    private String descriptionKey;

    @Column(name = "description_text", columnDefinition = "TEXT")
    private String descriptionText;

    @Column(name = "target_gender", length = 10)
    private String targetGender;
}
