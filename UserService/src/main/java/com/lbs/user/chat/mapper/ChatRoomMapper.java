package com.lbs.user.chat.mapper;

import com.lbs.user.chat.domain.ChatRoom;
import com.lbs.user.chat.domain.SenderType;
import com.lbs.user.chat.dto.request.CreateChatRoomRequestDto;
import com.lbs.user.chat.dto.response.ChatRoomResponseDto;
import com.lbs.user.chat.dto.response.ChatRoomSummaryResponseDto;
import com.lbs.user.chat.infrastructure.entity.ChatRoomEntity;
import com.lbs.user.user.domain.Role;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ChatRoomMapper {

    default ChatRoom createDtoDomain(CreateChatRoomRequestDto dto) {
        return ChatRoom.createChatRoom(dto.getAcademyId(), dto.getParentId());
    }

    default ChatRoomEntity domainToEntity(ChatRoom domain) {
        return ChatRoomEntity.builder()
                .id(domain.getId())
                .academyId(domain.getAcademyId())
                .parentId(domain.getParentId())
                .lastMessage(domain.getLastMessage())
                .lastMessageAt(domain.getLastMessageAt())
                .build();
    }

    ChatRoom entityToDomain(ChatRoomEntity entity);

    @Mapping(source = "id", target = "roomId")
    ChatRoomResponseDto domainToResponseDto(ChatRoom domain);

    default ChatRoomSummaryResponseDto toSummaryDto(ChatRoom chatRoom, Long requestUserId, Role role) {
        Long counterpartId = role == Role.PARENT ? chatRoom.getAcademyId() : chatRoom.getParentId();
        SenderType counterpartType = role == Role.PARENT ? SenderType.ACADEMY : SenderType.PARENT;
        return ChatRoomSummaryResponseDto.builder()
                .roomId(chatRoom.getId())
                .counterpartId(counterpartId)
                .counterpartType(counterpartType)
                .lastMessage(chatRoom.getLastMessage())
                .lastMessageAt(chatRoom.getLastMessageAt())
                .build();
    }
}
