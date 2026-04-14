package com.duri.durifront.notification.controller;

import java.util.List;
import java.util.Map;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import com.duri.durifront.like.service.MatchNotificationService;
import com.duri.durifront.notification.dto.NotificationResponse;
import com.duri.durifront.notification.service.NotificationService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
public class NotificationApiController {

    private final NotificationService notificationService;
    private final MatchNotificationService matchNotificationService;

    /** SSE 구독 */
    @GetMapping(value = "/subscribe/{userId}", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter subscribe(@PathVariable String userId) {
        return matchNotificationService.subscribe(userId);
    }

    /** 알림 목록 조회 */
    @GetMapping("/{userId}")
    public ResponseEntity<List<NotificationResponse>> getNotifications(@PathVariable String userId) {
        return ResponseEntity.ok(notificationService.getNotifications(userId));
    }

    /** 안읽은 알림 수 조회 */
    @GetMapping("/{userId}/unread-count")
    public ResponseEntity<Map<String, Long>> getUnreadCount(@PathVariable String userId) {
        return ResponseEntity.ok(Map.of("count", notificationService.getUnreadCount(userId)));
    }

    /** 전체 읽음 처리 */
    @PatchMapping("/{userId}/read-all")
    public ResponseEntity<Void> markAllRead(@PathVariable String userId) {
        notificationService.markAllRead(userId);
        return ResponseEntity.ok().build();
    }
}
