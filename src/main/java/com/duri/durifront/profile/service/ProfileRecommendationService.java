package com.duri.durifront.profile.service;

import java.util.Comparator;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.duri.durifront.avatar.entity.Avatar;
import com.duri.durifront.avatar.repository.AvatarRepository;
import com.duri.durifront.handphoto.entity.HandPhoto;
import com.duri.durifront.handphoto.repository.HandPhotoRepository;
import com.duri.durifront.profile.dto.ProfileDetailDto;
import com.duri.durifront.profile.dto.ProfileRecommendationDto;
import com.duri.durifront.profile.entity.Profile;
import com.duri.durifront.profile.repository.ProfileRepository;

import lombok.RequiredArgsConstructor;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ProfileRecommendationService {

	private final ProfileRepository profileRepository;
	private final AvatarRepository avatarRepository;
	private final HandPhotoRepository handPhotoRepository;

	/**
	 * 점수 기반 추천 프로필 목록
	 *
	 * 호환도 계산: face_preference 유사도 40% + absolute_score 30% + 취미 겹침 30%
	 */
	public List<ProfileRecommendationDto> getRecommendedProfiles(String userId, int page) {
		Profile myProfile = profileRepository.findByUserUserId(userId)
			.orElseThrow(() -> new IllegalArgumentException("프로필이 존재하지 않습니다."));

		List<Profile> candidates = profileRepository.findRecommendedProfiles(userId, myProfile.getGender(), java.time.LocalDateTime.now().minusDays(3));

		List<ProfileRecommendationDto> allRecommendations = candidates.stream()
			.map(candidate -> {
				int compatibility = calculateCompatibility(myProfile, candidate);
				String avatarUrl = avatarRepository.findByUserId(candidate.getUser().getUserId())
					.map(Avatar::getAvatarImageUrl).orElse(null);
				String handPhotoUrl = handPhotoRepository.findByUserId(candidate.getUser().getUserId())
					.map(HandPhoto::getHandImage).orElse(null);

				return ProfileRecommendationDto.builder()
					.profileId(candidate.getProfileId())
					.userId(candidate.getUser().getUserId())
					.nickname(candidate.getNickname())
					.age(calculateAge(candidate.getBirthDate()))
					.region(candidate.getRegion())
					.hobbies(candidate.getHobbies())
					.compatibility(compatibility)
					.avatarUrl(avatarUrl)
					.handPhotoUrl(handPhotoUrl)
					.absoluteScore(candidate.getAbsoluteScore())
					.build();
			})
			.sorted(Comparator.comparingInt(ProfileRecommendationDto::getCompatibility).reversed())
			.toList();

		int pageSize = 20;
		int fromIndex = page * pageSize;
		if (fromIndex >= allRecommendations.size()) {
			return java.util.Collections.emptyList();
		}
		int toIndex = Math.min(fromIndex + pageSize, allRecommendations.size());

		List<ProfileRecommendationDto> paged = new java.util.ArrayList<>(allRecommendations.subList(fromIndex, toIndex));
		java.util.Collections.shuffle(paged);
		return paged;
	}

	/**
	 * 프로필 상세 보기
	 */
	public ProfileDetailDto getProfileDetail(String userId) {
		Profile profile = profileRepository.findByUserUserId(userId)
			.orElseThrow(() -> new IllegalArgumentException("프로필이 존재하지 않습니다."));

		String avatarUrl = avatarRepository.findByUserId(userId)
			.map(Avatar::getAvatarImageUrl).orElse(null);
		String handPhotoUrl = handPhotoRepository
			.findByProfileIdAndStatus(profile.getProfileId(), HandPhoto.HandPhotoStatus.APPROVED)
			.map(HandPhoto::getHandImage).orElse(null);

		return ProfileDetailDto.builder()
			.profileId(profile.getProfileId())
			.userId(profile.getUser().getUserId())
			.nickname(profile.getNickname())
			.age(calculateAge(profile.getBirthDate()))
			.gender(profile.getGender())
			.region(profile.getRegion())
			.hobbies(profile.getHobbies())
			.faceFeatures(profile.getFaceFeatures())
			.facePreference(profile.getFacePreference())
			.additionalInformation(profile.getAdditionalInformation())
			.absoluteScore(profile.getAbsoluteScore() != null ? profile.getAbsoluteScore() : 50)
			.avatarUrl(avatarUrl)
			.handPhotoUrl(handPhotoUrl)
			.build();
	}

	// ─── 점수 계산 ───

	// 여자(false)가 볼 때 가중치
	private static final double FEMALE_BASE_WEIGHT = 0.40;       // 기본점수 40%
	private static final double FEMALE_ABSOLUTE_WEIGHT = 0.30;   // 절대점수 30%
	private static final double FEMALE_PREFERENCE_WEIGHT = 0.30; // 취향점수 30%

	// 남자(true)가 볼 때 가중치
	private static final double MALE_BASE_WEIGHT = 0.30;         // 기본점수 30%
	private static final double MALE_ABSOLUTE_WEIGHT = 0.40;     // 절대점수 40%
	private static final double MALE_PREFERENCE_WEIGHT = 0.30;   // 취향점수 30%

	// 기본점수 (모든 사용자에게 부여되는 기본 점수, 0~100 스케일)
	private static final double BASE_SCORE = 70.0;

	// 절대점수 반영 비율 (AI 점수의 30~40%만 실제 반영)
	private static final double ABSOLUTE_SCORE_MIN_RATIO = 0.30;
	private static final double ABSOLUTE_SCORE_MAX_RATIO = 0.40;

	/**
	 * 종합 호환도 (0~100)
	 *
	 * 성별에 따라 가중치가 다르게 적용됩니다.
	 *
	 * ── 여자가 볼 때 ──
	 *   기본점수 40% + 절대점수 30% + 취향점수 30%
	 *
	 * ── 남자가 볼 때 ──
	 *   기본점수 30% + 절대점수 40% + 취향점수 30%
	 *
	 * - 기본점수: 모든 사용자에게 기본으로 부여되는 점수
	 * - 절대점수: AI가 생성한 점수(0~100)의 30~40%를 반영
	 * - 취향점수: preference 이진 문자열(예: 0101001011) 매칭률
	 */
	private int calculateCompatibility(Profile myProfile, Profile other) {
		boolean isMale = Boolean.TRUE.equals(myProfile.getGender()); // true=남자, false=여자

		double baseWeight, absoluteWeight, preferenceWeight;
		if (isMale) {
			baseWeight = MALE_BASE_WEIGHT;
			absoluteWeight = MALE_ABSOLUTE_WEIGHT;
			preferenceWeight = MALE_PREFERENCE_WEIGHT;
		} else {
			baseWeight = FEMALE_BASE_WEIGHT;
			absoluteWeight = FEMALE_ABSOLUTE_WEIGHT;
			preferenceWeight = FEMALE_PREFERENCE_WEIGHT;
		}

		// 1. 기본점수 (0~100): 모든 사용자에게 기본으로 부여
		double baseScore = BASE_SCORE;

		// 2. 절대점수 (0~100): AI 점수의 30~40% 범위에서 매핑
		double rawAbsoluteScore = other.getAbsoluteScore() != null ? other.getAbsoluteScore() : 50;
		double absoluteScore = scaleAbsoluteScore(rawAbsoluteScore);

		// 3. 취향점수 (0~100): preference 이진 문자열 매칭
		double preferenceScore = preferenceMatchScore(myProfile.getFacePreference(), other.getFaceFeatures()) * 100;

		double totalScore = baseScore * baseWeight
			+ absoluteScore * absoluteWeight
			+ preferenceScore * preferenceWeight;

		return (int) Math.round(Math.min(totalScore, 100));
	}

	/**
	 * AI 절대점수를 30~40% 범위로 스케일링
	 * 예: AI 점수 80 → 80 * (0.30 ~ 0.40) = 24 ~ 32 → 평균 28
	 * 이를 다시 0~100 스케일로 변환: (28 / 40) * 100 = 70
	 */
	private double scaleAbsoluteScore(double rawScore) {
		double scaledMin = rawScore * ABSOLUTE_SCORE_MIN_RATIO;
		double scaledMax = rawScore * ABSOLUTE_SCORE_MAX_RATIO;
		double scaledAvg = (scaledMin + scaledMax) / 2.0;
		// 최대 가능 값(100 * 0.40 = 40)을 기준으로 0~100 스케일로 정규화
		return (scaledAvg / (100.0 * ABSOLUTE_SCORE_MAX_RATIO)) * 100.0;
	}

	/**
	 * 취향 preference 이진 문자열 매칭 (0.0 ~ 1.0)
	 * 예: "0101001011" vs "0100001111" → 8/10 = 0.8
	 */
	private double preferenceMatchScore(String myPreference, String otherFeatures) {
		if (myPreference == null || otherFeatures == null) return 0.0;
		int len = Math.min(myPreference.length(), otherFeatures.length());
		if (len == 0) return 0.0;
		int match = 0;
		for (int i = 0; i < len; i++) {
			if (myPreference.charAt(i) == otherFeatures.charAt(i)) match++;
		}
		return (double) match / len;	
	}

	private int calculateAge(java.time.LocalDate birthDate) {
		if (birthDate == null) return 0;
		return java.time.Period.between(birthDate, java.time.LocalDate.now()).getYears();
	}
}
