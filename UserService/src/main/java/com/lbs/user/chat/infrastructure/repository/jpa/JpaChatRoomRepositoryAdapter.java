package com.lbs.user.chat.infrastructure.repository.jpa;

import com.lbs.user.chat.domain.ChatRoom;
import com.lbs.user.chat.infrastructure.entity.ChatRoomEntity;
import com.lbs.user.chat.infrastructure.repository.ChatRoomRepository;
import com.lbs.user.chat.mapper.ChatRoomMapper;
import com.lbs.user.common.exception.ChatRoomNotFoundException;
import com.lbs.user.common.response.ErrorCode;
import com.lbs.user.user.domain.Role;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;

@Component
@RequiredArgsConstructor
@Transactional
@Slf4j
public class JpaChatRoomRepositoryAdapter implements ChatRoomRepository {

    private final JpaChatRoomRepository jpaChatRoomRepository;
    private final ChatRoomMapper chatRoomMapper;

    @Override
    public ChatRoom save(ChatRoom chatRoom) {
        ChatRoomEntity entity = chatRoomMapper.domainToEntity(chatRoom);
        return chatRoomMapper.entityToDomain(jpaChatRoomRepository.save(entity));
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<ChatRoom> findById(Long id) {
        return jpaChatRoomRepository.findById(id).map(chatRoomMapper::entityToDomain);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<ChatRoom> findByAcademyAndParent(Long academyId, Long parentId) {
        return jpaChatRoomRepository.findByAcademyIdAndParentId(academyId, parentId)
                .map(chatRoomMapper::entityToDomain);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ChatRoom> findAllByUser(Long userId, Role role, Pageable pageable) {
        if (role == Role.PARENT) {
            return jpaChatRoomRepository.findAllByParentIdOrderByLastMessageAtDesc(userId, pageable)
                    .map(chatRoomMapper::entityToDomain);
        }
        return jpaChatRoomRepository.findAllByAcademyIdOrderByLastMessageAtDesc(userId, pageable)
                .map(chatRoomMapper::entityToDomain);
    }

    @Override
    public void updateLastMessage(Long roomId, String content, LocalDateTime sentAt) {
        ChatRoomEntity entity = jpaChatRoomRepository.findById(roomId)
                .orElseThrow(() -> new ChatRoomNotFoundException(ErrorCode.CHAT_ROOM_NOT_FOUND));
        entity.updateLastMessage(content, sentAt);
    }
}
