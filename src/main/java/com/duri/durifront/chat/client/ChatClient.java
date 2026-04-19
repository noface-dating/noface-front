package com.duri.durifront.chat.client;

import com.duri.durifront.auth.annotation.UserId;
import com.duri.durifront.chat.dto.request.ChatRoomCreateRequestDTO;
import com.duri.durifront.chat.dto.response.ChatMessageResponseDTO;
import com.duri.durifront.chat.dto.response.ChatMessageSliceResponseDTO;
import com.duri.durifront.chat.dto.response.ChatRoomCreateResponseDTO;
import com.duri.durifront.chat.dto.response.ChatRoomSummaryResponseDTO;
import com.duri.durifront.global.config.FeignConfig;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.UUID;

@FeignClient(
        name = "chatListClient",
//        url = "${gateway.base-url}/api/v1/chat-room/",
        url = "http://localhost:8070/api/v1/chat-room/",
        configuration = FeignConfig.class
)
public interface ChatClient {

    @PostMapping
    ChatRoomCreateResponseDTO createChatRoom(@RequestBody ChatRoomCreateRequestDTO request);

    @GetMapping
    List<ChatRoomSummaryResponseDTO> getChatRooms(String userId);

    @GetMapping("/recent")
    List<ChatMessageResponseDTO> getRecentChatHistory(
            @RequestParam("roomId") UUID roomId,
            @RequestParam("size") Integer size
    );

    @GetMapping("/older")
    ChatMessageSliceResponseDTO getOlderChatHistory(
            @RequestParam("roomId") UUID roomId,
            @RequestParam UUID beforeCreatedAt
    );
}
