package com.lbs.user.chat.infrastructure.entity;

import com.lbs.user.user.infrastructure.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "chat_room",
       uniqueConstraints = @UniqueConstraint(columnNames = {"academy_id", "parent_id"}))
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Builder
@AllArgsConstructor
public class ChatRoomEntity extends BaseEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "academy_id", nullable = false)
    private Long academyId;

    @Column(name = "parent_id", nullable = false)
    private Long parentId;

    @Column(name = "last_message", length = 500)
    private String lastMessage;

    @Column(name = "last_message_at")
    private LocalDateTime lastMessageAt;

    public void updateLastMessage(String content, LocalDateTime sentAt) {
        this.lastMessage = content;
        this.lastMessageAt = sentAt;
    }
}
