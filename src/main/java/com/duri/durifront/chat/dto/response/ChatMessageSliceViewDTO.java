package com.duri.durifront.chat.dto.response;

import java.util.List;

public record ChatMessageSliceViewDTO(
        List<ChatMessageGroupResponseDTO> messages,
        boolean hasNext
) {
}
