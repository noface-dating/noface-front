package com.duri.durifront.handphoto.entity;

import com.duri.durifront.profile.entity.Profile;
import com.duri.durifront.user.entity.User;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "hand_photo")
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class HandPhoto {

	@EmbeddedId
	private HandPhotoId id;

	@MapsId("profileId")
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "profile_id")
	private Profile profile;

	@MapsId("userId")
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "user_id")
	private User user;

	@Column(name = "hand_image", nullable = false, length = 200)
	private String handImage;

	@Enumerated(EnumType.STRING)
	@Column(name = "status", nullable = false)
	private HandPhotoStatus status;

	public enum HandPhotoStatus {
		PENDING, APPROVED, REJECTED
	}
}