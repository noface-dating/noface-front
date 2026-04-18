package com.duri.durifront.avatar.entity;

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

	@Column(name = "avatar_image_url", nullable = false, length = 200)
	private String avatarImageUrl;

	@Column(name = "created_at", nullable = false)
	private LocalDateTime createdAt;
}
