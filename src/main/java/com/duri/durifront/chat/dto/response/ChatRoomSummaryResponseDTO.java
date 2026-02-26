package com.duri.durifront.chat.dto.response;

import java.util.UUID;

public record ChatRoomSummaryResponseDTO(
        UUID roomId,
        String roomName,
        boolean messageCheck
) {}
