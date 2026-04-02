package com.duri.durifront.avatar.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
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

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "avatar_id")
	private Long avatarId;

	@Column(name = "profile_id", nullable = false)
	private Long profileId;

	@Column(name = "user_id", nullable = false, length = 36)
	private String userId;

	@Column(name = "avatar_image_url", nullable = false, length = 200)
	private String avatarImageUrl;

	@Column(name = "created_at", nullable = false)
	private LocalDateTime createdAt;
}
