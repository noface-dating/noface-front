package com.duri.durifront.chat.dto.request;

import java.util.List;

public record ChatRoomCreateRequestDTO(
	String roomType,
	Long[] userIds
) {
	public static ChatRoomCreateRequestDTO of(Long user1Id, Long user2Id) {
		return new ChatRoomCreateRequestDTO("DM", new Long[]{user1Id, user2Id});
	}
}
