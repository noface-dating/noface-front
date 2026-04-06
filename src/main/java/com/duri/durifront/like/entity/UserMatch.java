package com.duri.durifront.like.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import java.util.UUID;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(
    name = "user_match",
    uniqueConstraints = @UniqueConstraint(columnNames = {"user1_id", "user2_id"})
)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class UserMatch {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "match_id")
    private Long matchId;

    /** 사전순 작은 userId (항상 user1 < user2 로 정규화하여 방향성 제거) */
    @Column(name = "user1_id", nullable = false)
    private String user1Id;

    /** 사전순 큰 userId */
    @Column(name = "user2_id", nullable = false)
    private String user2Id;

    @Column(name = "chat_room_id")
    private UUID chatRoomId;

    public static UserMatch of(String fromUserId, String toUserId, UUID chatRoomId) {
        String user1 = fromUserId.compareTo(toUserId) < 0 ? fromUserId : toUserId;
        String user2 = fromUserId.compareTo(toUserId) < 0 ? toUserId   : fromUserId;
        return new UserMatch(null, user1, user2, chatRoomId);
    }
}
