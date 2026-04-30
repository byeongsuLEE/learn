package com.lbs.user.chat.infrastructure.repository;

import com.lbs.user.chat.domain.ChatRoom;
import com.lbs.user.user.domain.Role;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.time.LocalDateTime;
import java.util.Optional;

public interface ChatRoomRepository {
    ChatRoom save(ChatRoom chatRoom);
    Optional<ChatRoom> findById(Long id);
    Optional<ChatRoom> findByAcademyAndParent(Long academyId, Long parentId);
    Page<ChatRoom> findAllByUser(Long userId, Role role, Pageable pageable);
    void updateLastMessage(Long roomId, String content, LocalDateTime sentAt);
}
