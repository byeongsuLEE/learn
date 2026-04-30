package com.lbs.user.chat.service;

import com.lbs.user.chat.domain.ChatMessage;
import com.lbs.user.chat.domain.SenderType;
import com.lbs.user.chat.dto.request.SendChatMessageRequestDto;
import com.lbs.user.chat.dto.response.ChatMessagePageResponseDto;
import com.lbs.user.chat.dto.response.ChatMessageResponseDto;
import com.lbs.user.chat.infrastructure.repository.ChatMessageRepository;
import com.lbs.user.chat.mapper.ChatMessageMapper;
import com.lbs.user.chat.service.ChatRoomService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class ChatMessageServiceImpl implements ChatMessageService {

    private final ChatMessageRepository chatMessageRepository;
    private final ChatRoomService chatRoomService;
    private final ChatMessageMapper chatMessageMapper;

    @Override
    @Transactional
    public ChatMessage saveMessage(Long roomId, Long senderId, SenderType senderType, SendChatMessageRequestDto request) {
        ChatMessage message = ChatMessage.builder()
                .roomId(roomId)
                .senderId(senderId)
                .senderType(senderType)
                .messageType(request.getMessageType())
                .content(request.getContent())
                .imageUrl(request.getImageUrl())
                .build();

        ChatMessage saved = chatMessageRepository.save(message);

        String displayContent = saved.getMessageType().name().equals("IMAGE") ? "[이미지]" : saved.getContent();
        LocalDateTime sentAt = saved.getCreatedAt() != null
                ? LocalDateTime.ofInstant(saved.getCreatedAt(), ZoneOffset.UTC)
                : LocalDateTime.now();
        chatRoomService.updateLastMessage(roomId, displayContent, sentAt);

        return saved;
    }

    @Override
    @Transactional(readOnly = true)
    public ChatMessagePageResponseDto getMessages(Long roomId, String cursor, int size, Long userId, SenderType senderType) {
        List<ChatMessage> messages = chatMessageRepository.findByRoomIdWithCursor(roomId, cursor, size);
        String nextCursor = messages.size() == size ? messages.get(messages.size() - 1).getId() : null;
        List<ChatMessageResponseDto> content = messages.stream()
                .map(chatMessageMapper::domainToResponseDto)
                .toList();
        return ChatMessagePageResponseDto.builder()
                .content(content)
                .nextCursor(nextCursor)
                .hasNext(nextCursor != null)
                .build();
    }
}
