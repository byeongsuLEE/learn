package com.lbs.user.chat.controller;

import com.lbs.user.chat.domain.ChatMessage;
import com.lbs.user.chat.domain.SenderType;
import com.lbs.user.chat.dto.request.SendChatMessageRequestDto;
import com.lbs.user.chat.dto.response.ChatMessageResponseDto;
import com.lbs.user.chat.mapper.ChatMessageMapper;
import com.lbs.user.chat.service.ChatMessageService;
import com.lbs.user.chat.service.ChatRoomService;
import com.lbs.user.user.domain.Role;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import java.security.Principal;

@Controller
@RequiredArgsConstructor
@Slf4j
public class ChatMessageController {

    private final ChatMessageService chatMessageService;
    private final ChatRoomService chatRoomService;
    private final ChatMessageMapper chatMessageMapper;
    private final SimpMessagingTemplate messagingTemplate;

    @MessageMapping("/chat/rooms/{roomId}/send")
    public void sendMessage(
            @DestinationVariable Long roomId,
            @Payload SendChatMessageRequestDto request,
            Principal principal) {

        if (principal == null) {
            log.warn("Unauthenticated STOMP message to room {}", roomId);
            return;
        }

        String[] parts = principal.getName().split(":");
        Long senderId = Long.parseLong(parts[0]);
        String roleStr = parts.length > 1 ? parts[1] : "PARENT";
        SenderType senderType = "ACADEMY".equalsIgnoreCase(roleStr) ? SenderType.ACADEMY : SenderType.PARENT;

        Role role = Role.valueOf(roleStr.toUpperCase());
        chatRoomService.getRoomForUser(roomId, senderId, role);

        ChatMessage saved = chatMessageService.saveMessage(roomId, senderId, senderType, request);
        ChatMessageResponseDto response = chatMessageMapper.domainToResponseDto(saved);

        messagingTemplate.convertAndSend("/topic/chat.rooms." + roomId, response);
        log.info("Message sent to room {}: senderId={}, type={}", roomId, senderId, request.getMessageType());
    }
}
