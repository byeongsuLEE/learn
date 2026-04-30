package com.lbs.user.chat.service;

import com.lbs.user.chat.domain.ChatMessage;
import com.lbs.user.chat.domain.SenderType;
import com.lbs.user.chat.dto.request.SendChatMessageRequestDto;
import com.lbs.user.chat.dto.response.ChatMessagePageResponseDto;

public interface ChatMessageService {
    ChatMessage saveMessage(Long roomId, Long senderId, SenderType senderType, SendChatMessageRequestDto request);
    ChatMessagePageResponseDto getMessages(Long roomId, String cursor, int size, Long userId, SenderType senderType);
}
