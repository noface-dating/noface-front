package com.duri.durifront.chat.dto.response;

import java.util.List;

public record ChatMessageSliceResponseDTO(
        List<ChatMessageResponseDTO> messages,
        boolean hasNext
) {
}
