package com.duri.durifront.like.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.duri.durifront.chat.client.ChatClient;
import com.duri.durifront.chat.dto.request.ChatRoomCreateRequestDTO;
import com.duri.durifront.chat.dto.response.ChatRoomCreateResponseDTO;
import com.duri.durifront.like.entity.UserLike;
import com.duri.durifront.like.entity.UserMatch;
import com.duri.durifront.like.repository.UserLikeRepository;
import com.duri.durifront.like.repository.UserMatchRepository;
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
	private final UserMatchRepository userMatchRepository;
	private final UserRepository userRepository;
	private final ChatClient chatClient;
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

			// 채팅방 생성 요청
			UUID roomId = null;
			ChatRoomCreateRequestDTO chatRequest = ChatRoomCreateRequestDTO.of(fromUserId, toUserId);
			log.info("[채팅방 생성 요청] POST /api/v1/chat-room/ | body={}", chatRequest);
			try {
				ChatRoomCreateResponseDTO chatResponse = chatClient.createChatRoom(chatRequest);
				roomId = chatResponse.chatRoomId();
				log.info("[채팅방 생성 성공] roomId={}", roomId);
			} catch (Exception e) {
				log.warn("[채팅방 생성 실패] 채팅 서버 미응답 또는 오류 - 매칭은 정상 처리됨. error={}", e.getMessage());
			}

			// 4. UserMatch 저장 - UNIQUE(user1_id, user2_id) 제약으로 동시 매칭 시 하나만 저장됨
			// 동시 요청에서 두 트랜잭션 모두 이 지점에 도달해도, DB 유니크 제약이 중복 INSERT를 차단하여
			// 채팅방 알림이 한 번만 전송되는 것을 보장한다.
			try {
				userMatchRepository.save(UserMatch.of(fromUserId, toUserId, roomId));
				notificationService.sendMatchNotification(fromUserId, toUserId, roomId);
			} catch (DataIntegrityViolationException e) {
				log.warn("[중복 매칭 방지] 이미 처리된 매칭입니다. fromUser={}, toUser={}", fromUserId, toUserId);
			}

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
