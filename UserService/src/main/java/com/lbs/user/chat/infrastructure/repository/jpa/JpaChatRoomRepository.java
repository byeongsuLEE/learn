package com.lbs.user.chat.infrastructure.repository.jpa;

import com.lbs.user.chat.infrastructure.entity.ChatRoomEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface JpaChatRoomRepository extends JpaRepository<ChatRoomEntity, Long> {
    Optional<ChatRoomEntity> findByAcademyIdAndParentId(Long academyId, Long parentId);
    Page<ChatRoomEntity> findAllByParentIdOrderByLastMessageAtDesc(Long parentId, Pageable pageable);
    Page<ChatRoomEntity> findAllByAcademyIdOrderByLastMessageAtDesc(Long academyId, Pageable pageable);
}
