package com.lbs.user.chat.infrastructure.repository.mongo;

import com.lbs.user.chat.domain.ChatMessage;
import com.lbs.user.chat.infrastructure.repository.ChatMessageRepository;
import com.lbs.user.chat.mapper.ChatMessageMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Limit;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class MongoChatMessageRepositoryAdapter implements ChatMessageRepository {

    private final MongoChatMessageRepository mongoChatMessageRepository;
    private final ChatMessageMapper chatMessageMapper;

    @Override
    public ChatMessage save(ChatMessage message) {
        return chatMessageMapper.documentToDomain(
                mongoChatMessageRepository.save(chatMessageMapper.domainToDocument(message))
        );
    }

    @Override
    public List<ChatMessage> findByRoomIdWithCursor(Long roomId, String cursor, int size) {
        Limit limit = Limit.of(size);
        if (cursor == null || cursor.isBlank()) {
            return mongoChatMessageRepository.findByRoomIdOrderByCreatedAtDesc(roomId, limit)
                    .stream().map(chatMessageMapper::documentToDomain).toList();
        }
        return mongoChatMessageRepository.findByRoomIdAndIdLessThanOrderByCreatedAtDesc(roomId, cursor, limit)
                .stream().map(chatMessageMapper::documentToDomain).toList();
    }
}
