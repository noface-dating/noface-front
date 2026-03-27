package com.duri.durifront.profile.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProfileDetailDto {
	private Long profileId;
	private Long userId;
	private String nickname;
	private int age;
	private Boolean gender;
	private String region;
	private String hobbies;
	private String faceFeatures;
	private String facePreference;
	private String additionalInformation;
	private int absoluteScore;
	private String avatarUrl;
	private String handPhotoUrl;
}
