package com.lbs.user.chat.service;

import com.lbs.user.chat.domain.ChatRoom;
import com.lbs.user.chat.dto.request.CreateChatRoomRequestDto;
import com.lbs.user.chat.infrastructure.repository.ChatRoomRepository;
import com.lbs.user.chat.mapper.ChatRoomMapper;
import com.lbs.user.common.exception.ChatRoomForbiddenException;
import com.lbs.user.common.exception.ChatRoomNotFoundException;
import com.lbs.user.common.response.ErrorCode;
import com.lbs.user.user.domain.Role;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Slf4j
public class ChatRoomServiceImpl implements ChatRoomService {

    private final ChatRoomRepository chatRoomRepository;
    private final ChatRoomMapper chatRoomMapper;

    @Override
    @Transactional
    public ChatRoom createOrGetRoom(CreateChatRoomRequestDto request) {
        return chatRoomRepository.findByAcademyAndParent(request.getAcademyId(), request.getParentId())
                .orElseGet(() -> chatRoomRepository.save(chatRoomMapper.createDtoDomain(request)));
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ChatRoom> getMyRooms(Long userId, Role role, Pageable pageable) {
        return chatRoomRepository.findAllByUser(userId, role, pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public ChatRoom getRoomForUser(Long roomId, Long userId, Role role) {
        ChatRoom room = chatRoomRepository.findById(roomId)
                .orElseThrow(() -> new ChatRoomNotFoundException(ErrorCode.CHAT_ROOM_NOT_FOUND));
        boolean isParticipant = role == Role.PARENT
                ? room.getParentId().equals(userId)
                : room.getAcademyId().equals(userId);
        if (!isParticipant) {
            throw new ChatRoomForbiddenException(ErrorCode.CHAT_ROOM_FORBIDDEN);
        }
        return room;
    }

    @Override
    @Transactional
    public void updateLastMessage(Long roomId, String content, LocalDateTime sentAt) {
        chatRoomRepository.updateLastMessage(roomId, content, sentAt);
    }
}
