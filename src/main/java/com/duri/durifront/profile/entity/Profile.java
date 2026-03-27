package com.duri.durifront.profile.entity;

import java.time.LocalDate;

import com.duri.durifront.user.entity.User;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "profile")
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class Profile {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "profile_id")
	private Long profileId;

	@OneToOne
	@JoinColumn(name = "user_id")
	private User user;

	@Column(name = "nickname", nullable = false, length = 50)
	private String nickname;

	@Column(name = "birth_date", nullable = false)
	private LocalDate birthDate;

	@Column(name = "gender", nullable = false)
	private Boolean gender; // true=남, false=여

	@Column(name = "region", length = 50)
	private String region;

	/** 취미 (JSON 컬럼) - 예: ["#음악","#여행","#영화"] */
	@Column(name = "hobbies", columnDefinition = "JSON")
	private String hobbies;

	/** 내 얼굴 특징 코드 (10자리) */
	@Column(name = "face_features", columnDefinition = "CHAR(10)", nullable = false, length = 10)
	private String faceFeatures;

	/** 내가 선호하는 얼굴 코드 (10자리) - 이상형 월드컵 결과 */
	@Column(name = "face_preference", columnDefinition = "CHAR(10)", nullable = false, length = 10)
	private String facePreference;

	/** 추가 정보 (JSON) - MBTI, 직업, 자기소개 등 */
	@Column(name = "additional_information", columnDefinition = "JSON")
	private String additionalInformation;

	/** 절대 점수 (0~100) */
	@Column(name = "absolute_score", columnDefinition = "TINYINT", nullable = false)
	private Integer absoluteScore;
}
