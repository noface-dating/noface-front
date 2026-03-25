package com.duri.durifront.profile.entity;

import static lombok.AccessLevel.PROTECTED;

import com.duri.durifront.user.entity.User;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Objects;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Table(name = "profile")
@NoArgsConstructor(access = PROTECTED)
@Entity
public class Profile {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "profile_id")
    private Long profileId;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", referencedColumnName = "user_id", unique = true)
    private User user;

    @Column(nullable = false, length = 50)
    private String nickname;

    @Column(name = "birth_date", nullable = false)
    private LocalDate birthDate;

    @Column(nullable = false)
    private Boolean gender;

    @Column(length = 50)
    private String region;

    @Column(columnDefinition = "JSON")
    private String hobbies;

    @Column(name = "face_features", nullable = false, length = 10)
    private String faceFeatures;

    @Column(name = "face_preference", nullable = false, length = 10)
    private String facePreference;

    @Column(name = "additional_information", columnDefinition = "JSON")
    private String additionalInformation;

    @Column(name = "absolute_score", nullable = false)
    private Byte absoluteScore;

    public static Profile createProfile(User user,
                                        String nickname,
                                        LocalDate birthDate,
                                        Boolean gender,
                                        String region,
                                        String hobbies,
                                        String faceFeatures,
                                        String facePreference,
                                        String additionalInformation,
                                        Byte absoluteScore)
    {
        Profile profile = new Profile();
        profile.setUser(user);
        profile.setNickname(nickname);
        profile.setBirthDate(birthDate);
        profile.setGender(gender);
        profile.setRegion(region);
        profile.setHobbies(Objects.isNull(hobbies) ? "[]" : hobbies);
        profile.setFaceFeatures(faceFeatures);
        profile.setFacePreference(facePreference);
        profile.setAdditionalInformation(Objects.isNull(additionalInformation) ? "[]" : additionalInformation);
        profile.setAbsoluteScore(absoluteScore);

        return profile;
    }

}
