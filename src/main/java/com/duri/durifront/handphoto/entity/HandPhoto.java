package com.duri.durifront.handphoto.entity;

import com.duri.durifront.handphoto.entity.HandPhotoId;
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

	@Column(name = "hand_image", nullable = false, length = 200)
	private String handImage;

	@Enumerated(EnumType.STRING)
	@Column(name = "status", nullable = false)
	private HandPhotoStatus status;

	public enum HandPhotoStatus {
		PENDING, APPROVED, REJECTED
	}
}