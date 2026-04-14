package com.duri.durifront.notification.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "notification")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Notification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "notification_id")
    private Long id;

    /** 알림을 받는 사용자 ID */
    @Column(name = "user_id", nullable = false, length = 36)
    private String userId;

    /** 매칭 상대방 ID */
    @Column(name = "partner_id", nullable = false, length = 36)
    private String partnerId;

    /** 알림 타입 (현재는 MATCH만 사용) */
    @Column(name = "type", nullable = false, length = 20)
    private String type;

    @Column(name = "message", nullable = false)
    private String message;

    @Column(name = "is_read", nullable = false)
    private boolean read = false;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Builder
    private Notification(String userId, String partnerId, String type, String message) {
        this.userId = userId;
        this.partnerId = partnerId;
        this.type = type;
        this.message = message;
        this.read = false;
        this.createdAt = LocalDateTime.now();
    }

    public static Notification matchNotification(String userId, String partnerId) {
        return Notification.builder()
            .userId(userId)
            .partnerId(partnerId)
            .type("MATCH")
            .message("새로운 매칭이 성사되었어요! 🎉")
            .build();
    }

    public void markAsRead() {
        this.read = true;
    }
}
