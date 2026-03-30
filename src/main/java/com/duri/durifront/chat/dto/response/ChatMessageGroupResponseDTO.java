package com.duri.durifront.chat.dto.response;

import java.time.LocalDateTime;
import java.util.List;

public record ChatMessageGroupResponseDTO(
        LocalDateTime dateTime,
        List<ChatMessageResponseDTO> messages
) {
}
