package com.duri.durifront.like.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.duri.durifront.chat.dto.request.ChatRoomCreateRequestDTO;
import com.duri.durifront.like.repository.UserLikeRepository;
import com.duri.durifront.like.entity.UserLike;
import com.duri.durifront.like.service.MatchNotificationService;
import com.duri.durifront.user.entity.User;
import com.duri.durifront.user.repository.UserRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class LikeService {

	private final UserLikeRepository userLikeRepository;
	private final UserRepository userRepository;
	// private final ChatClient chatClient;  // ChatClient 클래스가 아직 없으므로 주석 처리
	private final MatchNotificationService notificationService;

	/**
	 * 좋아요 전송
	 *
	 * @return LikeResult (성공 여부, 매칭 여부)
	 */
	public LikeResult sendLike(String fromUserId, String toUserId) {
		// 1. 중복 체크
		Optional<UserLike> existing = userLikeRepository
			.findByFromUser_UserIdAndToUser_UserId(fromUserId, toUserId);
		if (existing.isPresent()) {
			return LikeResult.ofFail("이미 반응한 프로필이에요.");
		}

		// 2. 저장
		User fromUser = userRepository.findById(fromUserId)
			.orElseThrow(() -> new IllegalArgumentException("Invalid from user ID"));
		User toUser = userRepository.findById(toUserId)
			.orElseThrow(() -> new IllegalArgumentException("Invalid to user ID"));

		UserLike like = UserLike.builder()
			.fromUser(fromUser)
			.toUser(toUser)
			.likedAt(LocalDateTime.now())
			.build();
		userLikeRepository.save(like);

		// 3. 매칭 체크 (서로 좋아요를 보냈는지)
		Optional<UserLike> mutualLikeOpt = userLikeRepository
			.findByFromUser_UserIdAndToUser_UserId(toUserId, fromUserId);

		if (mutualLikeOpt.isPresent()) {
			log.info("매칭 성공! user1={}, user2={}", fromUserId, toUserId);
			UserLike mutualLike = mutualLikeOpt.get();

			// 상태 변경
			like.updateMatched();
			mutualLike.updateMatched();

			// TODO: ChatClient가 구현되면 채팅방 생성 로직 활성화
			// UUID roomId = chatClient.createDMChatRoom(
			// 	ChatRoomCreateRequestDTO.of(fromUserId, toUserId)
			// );

			// 알림 전송 (채팅방 ID 없이 일단 전송)
			notificationService.sendMatchNotification(fromUserId, toUserId, null);

			return LikeResult.ofMatched();
		}

		return LikeResult.ofSuccess();
	}

	/**
	 * 내가 보낸 좋아요 목록 (userId 리스트)
	 */
	@Transactional(readOnly = true)
	public List<String> getLikedUserIds(String userId) {
		return userLikeRepository
			.findByFromUser_UserId(userId)
			.stream()
			.map(userLike -> userLike.getToUser().getUserId())
			.toList();
	}

	/**
	 * 나를 좋아한 사용자 목록 (userId 리스트)
	 */
	@Transactional(readOnly = true)
	public List<String> getLikedByUserIds(String userId) {
		return userLikeRepository
			.findByToUser_UserId(userId)
			.stream()
			.map(userLike -> userLike.getFromUser().getUserId())
			.toList();
	}

	// ─── LikeResult inner class ───

	public record LikeResult(boolean success, boolean matched, String error) {
		public static LikeResult ofSuccess() { return new LikeResult(true, false, null); }
		public static LikeResult ofMatched() { return new LikeResult(true, true, null); }
		public static LikeResult ofFail(String error) { return new LikeResult(false, false, error); }
	}
}
