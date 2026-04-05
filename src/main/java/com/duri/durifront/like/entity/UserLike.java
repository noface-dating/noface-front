package com.duri.durifront.like.entity;

import java.time.LocalDateTime;

import com.duri.durifront.user.entity.User;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "`like`") // 예약어 방지
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class UserLike {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "like_id")
	private Long likeId;

	/** 좋아요를 보낸 사용자 */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "like_send_id", nullable = false)
	private User fromUser;

	/** 좋아요를 받은 사용자 */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "like_receive_id", nullable = false)
	private User toUser;

	@Column(name = "liked_at", nullable = false)
	private LocalDateTime likedAt;

	@Column(name = "matched", nullable = false)
	@Builder.Default
	private Boolean matched = false;

	public void updateMatched() {
		this.matched = true;
	}
}
