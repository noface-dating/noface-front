package com.duri.durifront.chat.controller;

import com.duri.durifront.chat.dto.response.ChatMessageGroupResponseDTO;
import com.duri.durifront.chat.dto.response.ChatMessageSliceViewDTO;
import com.duri.durifront.chat.service.ChatService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

/**
 * 채팅방 기능에 대한 Controller Layer입니다.
 *
 * @author kyw10987
 */
@Slf4j
@Controller
@RequiredArgsConstructor
@RequestMapping("/chat")
public class ChatController {

    private final ChatService chatService;

    /**
     * 사용자의 채팅방 리스트를 반환하는 메소드입니다.
     *
     * @param model Spring model
     * @return 채팅방 리스트 html view를 반환합니다.
     */
    @GetMapping
    public String chatList(Model model) {
        model.addAttribute("chatRooms", chatService.getMyChatRooms());
        return "chat/chat-list";
    }

    /**
     * 사용자가 요청한 채팅방에 입장하는 메소드입니다.
     *
     * @param roomId roomId uniqueID
     * @param model Spring model
     * @return 채팅방에 대한 HTML view를 반환
     */
    @GetMapping("/room/{roomId}")
    public String chatRoom(
            @PathVariable UUID roomId,
            Model model) {
        model.addAttribute("userId", 1);
        model.addAttribute("roomId", roomId.toString());
        return "chat/chat-room";
    }

    /**
     * 채팅방의 채팅들을 조회하는 메소드입니다. 모든 채팅들은 여기서 조회됩니다.
     *
     * @param roomId roomId uniqueID
     * @param before 기준 시점
     * @param size 한번에 가져올 채팅 갯수
     * @return 채팅(json)
     */
    @GetMapping(path = "/room/{roomId}/chat")
    @ResponseBody
    public ChatMessageSliceViewDTO getOlderMessages(
            @PathVariable UUID roomId,
            @RequestParam(required = false) UUID before,
            @RequestParam(defaultValue = "50") Integer size
    ) {
        if (before == null) {
            return new ChatMessageSliceViewDTO(chatService.getRecentChatMessage(roomId, size), true);
        }
        return chatService.getOlderChatMessage(roomId, before, size);
    }
}
