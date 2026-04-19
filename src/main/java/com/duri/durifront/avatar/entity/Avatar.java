package com.duri.durifront.avatar.entity;

import com.duri.durifront.profile.entity.Profile;
import com.duri.durifront.user.entity.User;
import java.time.LocalDateTime;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "avatar")
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class Avatar {

	@EmbeddedId
	private AvatarId id;

	@MapsId("profileId")
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "profile_id")
	private Profile profile;

	@MapsId("userId")
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "user_id")
	private User user;

	@Column(name = "avatar_image_url", nullable = false, length = 200)
	private String avatarImageUrl;

	@Column(name = "created_at", nullable = false)
	private LocalDateTime createdAt;
}
