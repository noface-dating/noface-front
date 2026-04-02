package com.duri.durifront.chat.service;

import com.duri.durifront.chat.client.ChatClient;
import com.duri.durifront.chat.dto.response.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Feign Client에 붙어있는 채팅 Service Layer 입니다.
 *
 * @author kyw10987
 */
@Service
@RequiredArgsConstructor
public class ChatService {

    private final ChatClient chatClient;

    //내 채팅방 리스트 반환

    /**
     * 사용자가 속한 채팅방 을 반환합니다.
     *
     * @return 사용자가 속한 채팅방 리스트를 반환
     */
    public List<ChatRoomSummaryResponseDTO> getMyChatRooms() {
        return chatClient.getChatRooms();
    }

    //채팅방 접속시 처음 메시지 로드

    /**
     * 채팅방 접속시 바로 조회되는 메시지를 로드하는 메소드입니다.
     *
     * @param roomId roomId uniqueID
     * @param size 조회할 메시지 양 (null이면 기본값 50)
     * @return 채팅
     */
    public List<ChatMessageGroupResponseDTO> getRecentChatMessage(UUID roomId, Integer size) {
        List<ChatMessageResponseDTO> messages = chatClient.getRecentChatHistory(roomId, size);

        return messages.stream()
                .collect(Collectors.groupingBy(msg -> msg.timestamp().toLocalDate(),
                        LinkedHashMap::new, Collectors.toList()))
                .entrySet().stream()
                .map(entry -> new ChatMessageGroupResponseDTO(
                        entry.getKey().atStartOfDay(),
                        entry.getValue()
                ))
                .toList();
    }

    /**
     * 특정 시점 이전의 채팅을 조회하는 메소드
     *
     * @param roomId roomId uniqueID
     * @param beforeCreatedAt 기준 시점
     * @param size 조회할 메시지 양 (null이면 기본값 50)
     * @return 채팅
     */
    public ChatMessageSliceViewDTO getOlderChatMessage(UUID roomId, UUID beforeCreatedAt, Integer size) {
        ChatMessageSliceResponseDTO messages = chatClient.getOlderChatHistory(roomId, beforeCreatedAt);

        Map<LocalDate, List<ChatMessageResponseDTO>> groupedMap = messages.messages().stream()
                .collect(Collectors.groupingBy(msg -> msg.timestamp().toLocalDate(),
                        LinkedHashMap::new, Collectors.toList()));

        List<ChatMessageGroupResponseDTO> groupedMessages = groupedMap.entrySet().stream()
                .map(entry -> new ChatMessageGroupResponseDTO(
                        entry.getKey().atStartOfDay(),
                        entry.getValue()
                ))
                .toList();

        return new ChatMessageSliceViewDTO(groupedMessages, messages.hasNext());
    }

}
