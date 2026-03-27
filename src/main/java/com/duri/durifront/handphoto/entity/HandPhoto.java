package com.duri.durifront.handphoto.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
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
@Table(name = "hand_photo")
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class HandPhoto {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "hand_photo_id")
	private Long handPhotoId;

	@Column(name = "profile_id", nullable = false)
	private Long profileId;

	@Column(name = "user_id", nullable = false)
	private Long userId;

	@Column(name = "hand_image", nullable = false, length = 200)
	private String handImage;

	@Enumerated(EnumType.STRING)
	@Column(name = "status", nullable = false)
	private HandPhotoStatus status;

	public enum HandPhotoStatus {
		PENDING, APPROVED, REJECTED
	}
}
