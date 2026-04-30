package com.lbs.user.chat.service;

import com.lbs.user.chat.domain.ChatRoom;
import com.lbs.user.chat.dto.request.CreateChatRoomRequestDto;
import com.lbs.user.user.domain.Role;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.time.LocalDateTime;

public interface ChatRoomService {
    ChatRoom createOrGetRoom(CreateChatRoomRequestDto request);
    Page<ChatRoom> getMyRooms(Long userId, Role role, Pageable pageable);
    ChatRoom getRoomForUser(Long roomId, Long userId, Role role);
    void updateLastMessage(Long roomId, String content, LocalDateTime sentAt);
}
