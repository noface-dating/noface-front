package com.duri.durifront.notification.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.duri.durifront.notification.dto.NotificationResponse;
import com.duri.durifront.notification.entity.Notification;
import com.duri.durifront.notification.repository.NotificationRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class NotificationService {

    private final NotificationRepository notificationRepository;

    /**
     * 매칭 알림을 양쪽 유저 모두에게 저장
     */
    @Transactional
    public void saveMatchNotifications(String user1Id, String user2Id) {
        notificationRepository.save(Notification.matchNotification(user1Id, user2Id));
        notificationRepository.save(Notification.matchNotification(user2Id, user1Id));
    }

    /**
     * 사용자의 알림 목록 조회 (최신순)
     */
    @Transactional(readOnly = true)
    public List<NotificationResponse> getNotifications(String userId) {
        return notificationRepository.findByUserIdOrderByCreatedAtDesc(userId)
            .stream()
            .map(NotificationResponse::from)
            .toList();
    }

    /**
     * 안읽은 알림 수 조회
     */
    @Transactional(readOnly = true)
    public long getUnreadCount(String userId) {
        return notificationRepository.countByUserIdAndReadFalse(userId);
    }

    /**
     * 사용자의 모든 알림 읽음 처리
     */
    @Transactional
    public void markAllRead(String userId) {
        notificationRepository.markAllReadByUserId(userId);
    }
}
