package com.duri.durifront.like.service;

import java.io.IOException;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import com.duri.durifront.notification.service.NotificationService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * SSE(Server-Sent Events) 기반 매칭 알림 서비스.
 *
 * 클라이언트가 GET /api/notifications/subscribe/{userId}로 구독하면
 * SseEmitter를 반환하고, 매칭 시 해당 유저에게 이벤트를 푸시합니다.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class MatchNotificationService {

	/** userId → SseEmitter 매핑 */
	private final Map<String, SseEmitter> emitters = new ConcurrentHashMap<>();
	private final NotificationService notificationService;
	private static final long SSE_TIMEOUT = 30 * 60 * 1000L; // 30분

	/**
	 * SSE 구독 등록
	 */
	public SseEmitter subscribe(String userId) {
		SseEmitter emitter = new SseEmitter(SSE_TIMEOUT);

		emitters.put(userId, emitter);

		emitter.onCompletion(() -> emitters.remove(userId));
		emitter.onTimeout(() -> emitters.remove(userId));
		emitter.onError((e) -> emitters.remove(userId));

		// 연결 직후 더미 이벤트 전송 (연결 유지)
		try {
			emitter.send(SseEmitter.event()
				.name("connect")
				.data("SSE 연결 성공"));
		} catch (IOException e) {
			emitters.remove(userId);
		}

		return emitter;
	}

	/**
	 * 매칭 성공 알림 전송 (양쪽 유저 모두에게)
	 * DB 저장 + SSE 실시간 전송 병행
	 */
	public void sendMatchNotification(String user1Id, String user2Id, UUID roomId) {
		// DB에 알림 저장 (SSE 연결 여부와 무관하게 항상 저장)
		notificationService.saveMatchNotifications(user1Id, user2Id);

		// SSE 실시간 전송 (연결 중인 경우에만)
		String roomIdStr = roomId != null ? roomId.toString() : "";
		String payload = String.format(
			"{\"roomId\": \"%s\", \"partnerId\": \"%s\", \"message\": \"새로운 매칭이 성사되었어요! 🎉\"}", roomIdStr, user2Id);
		sendToUser(user1Id, "match", payload);

		String payload2 = String.format(
			"{\"roomId\": \"%s\", \"partnerId\": \"%s\", \"message\": \"새로운 매칭이 성사되었어요! 🎉\"}", roomIdStr, user1Id);
		sendToUser(user2Id, "match", payload2);
	}

	private void sendToUser(String userId, String eventName, String data) {
		SseEmitter emitter = emitters.get(userId);
		if (emitter == null) {
			log.debug("SSE emitter not found for userId={}", userId);
			return;
		}
		try {
			emitter.send(SseEmitter.event()
				.name(eventName)
				.data(data));
			log.info("SSE 알림 전송: userId={}, event={}", userId, eventName);
		} catch (IOException e) {
			log.warn("SSE 전송 실패: userId={}", userId, e);
			emitters.remove(userId);
		}
	}
}
