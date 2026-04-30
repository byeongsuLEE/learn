package com.lbs.user.chat.infrastructure.repository;

import com.lbs.user.chat.domain.ChatMessage;
import java.util.List;

public interface ChatMessageRepository {
    ChatMessage save(ChatMessage message);
    List<ChatMessage> findByRoomIdWithCursor(Long roomId, String cursor, int size);
}
