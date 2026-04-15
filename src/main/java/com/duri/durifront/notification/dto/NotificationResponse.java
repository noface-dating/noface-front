package com.duri.durifront.notification.dto;

import java.time.LocalDateTime;

import com.duri.durifront.notification.entity.Notification;

public record NotificationResponse(
    Long id,
    String userId,
    String partnerId,
    String type,
    String message,
    boolean read,
    LocalDateTime createdAt
) {
    public static NotificationResponse from(Notification n) {
        return new NotificationResponse(
            n.getId(),
            n.getUserId(),
            n.getPartnerId(),
            n.getType(),
            n.getMessage(),
            n.isRead(),
            n.getCreatedAt()
        );
    }
}
