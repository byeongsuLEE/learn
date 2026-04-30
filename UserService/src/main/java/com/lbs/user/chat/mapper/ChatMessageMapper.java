package com.lbs.user.chat.mapper;

import com.lbs.user.chat.domain.ChatMessage;
import com.lbs.user.chat.dto.response.ChatMessageResponseDto;
import com.lbs.user.chat.infrastructure.entity.ChatMessageDocument;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ChatMessageMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    ChatMessageDocument domainToDocument(ChatMessage domain);

    ChatMessage documentToDomain(ChatMessageDocument document);

    ChatMessageResponseDto domainToResponseDto(ChatMessage domain);
}
