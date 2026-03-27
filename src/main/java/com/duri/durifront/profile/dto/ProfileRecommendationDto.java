package com.duri.durifront.profile.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProfileRecommendationDto {
	private Long profileId;
	private Long userId;
	private String nickname;
	private int age;
	private String region;
	private String hobbies;
	private int compatibility;
	private String avatarUrl;
	private String handPhotoUrl;
	private int absoluteScore;
}
