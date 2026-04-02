package com.duri.durifront.chat.dto.request;

import java.util.List;

public record ChatRoomCreateRequestDTO(
	String roomType,
	String[] userIds
) {
	public static ChatRoomCreateRequestDTO of(String user1Id, String user2Id) {
		return new ChatRoomCreateRequestDTO("DM", new String[]{user1Id, user2Id});
	}
}
