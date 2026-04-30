package com.lbs.user.chat.controller;

import com.lbs.user.chat.domain.ChatRoom;
import com.lbs.user.chat.domain.SenderType;
import com.lbs.user.chat.dto.request.CreateChatRoomRequestDto;
import com.lbs.user.chat.dto.request.PresignedUrlRequestDto;
import com.lbs.user.chat.dto.response.ChatMessagePageResponseDto;
import com.lbs.user.chat.dto.response.ChatRoomPageResponseDto;
import com.lbs.user.chat.dto.response.ChatRoomResponseDto;
import com.lbs.user.chat.dto.response.ChatRoomSummaryResponseDto;
import com.lbs.user.chat.dto.response.PresignedUrlResponseDto;
import com.lbs.user.chat.mapper.ChatMessageMapper;
import com.lbs.user.chat.mapper.ChatRoomMapper;
import com.lbs.user.chat.service.ChatMessageService;
import com.lbs.user.chat.service.ChatRoomService;
import com.lbs.user.chat.service.MinioStorageService;
import com.lbs.user.common.response.ApiResponse;
import com.lbs.user.user.domain.Role;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/chat")
@RequiredArgsConstructor
@Slf4j
public class ChatRoomController {

    private final ChatRoomService chatRoomService;
    private final ChatRoomMapper chatRoomMapper;
    private final ChatMessageService chatMessageService;
    private final ChatMessageMapper chatMessageMapper;
    private final MinioStorageService minioStorageService;

    @PostMapping("/rooms/create")
    public ResponseEntity<ApiResponse<ChatRoomResponseDto>> createRoom(
            @RequestBody @Valid CreateChatRoomRequestDto request) {
        ChatRoom chatRoom = chatRoomService.createOrGetRoom(request);
        return ResponseEntity.status(HttpStatus.OK)
                .body(ApiResponse.success(HttpStatus.OK, "채팅방 생성 완료",
                        chatRoomMapper.domainToResponseDto(chatRoom)));
    }

    @GetMapping("/rooms")
    public ResponseEntity<ApiResponse<ChatRoomPageResponseDto>> getMyRooms(
            @RequestParam Long userId,
            @RequestParam(defaultValue = "PARENT") String role,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        Role userRole = Role.valueOf(role.toUpperCase());
        Page<ChatRoom> rooms = chatRoomService.getMyRooms(userId, userRole, PageRequest.of(page, size));
        List<ChatRoomSummaryResponseDto> content = rooms.getContent().stream()
                .map(r -> chatRoomMapper.toSummaryDto(r, userId, userRole))
                .toList();
        ChatRoomPageResponseDto response = ChatRoomPageResponseDto.builder()
                .content(content)
                .totalElements(rooms.getTotalElements())
                .page(rooms.getNumber())
                .size(rooms.getSize())
                .build();
        return ResponseEntity.status(HttpStatus.OK)
                .body(ApiResponse.success(HttpStatus.OK, response));
    }

    @GetMapping("/rooms/{room_id}/messages")
    public ResponseEntity<ApiResponse<ChatMessagePageResponseDto>> getMessages(
            @PathVariable("room_id") Long roomId,
            @RequestParam Long userId,
            @RequestParam(defaultValue = "PARENT") String role,
            @RequestParam(required = false) String cursor,
            @RequestParam(defaultValue = "30") int size) {
        Role userRole = Role.valueOf(role.toUpperCase());
        chatRoomService.getRoomForUser(roomId, userId, userRole);
        SenderType senderType = userRole == Role.PARENT ? SenderType.PARENT : SenderType.ACADEMY;
        ChatMessagePageResponseDto response = chatMessageService.getMessages(roomId, cursor, size, userId, senderType);
        return ResponseEntity.status(HttpStatus.OK)
                .body(ApiResponse.success(HttpStatus.OK, response));
    }

    @PostMapping("/uploads/presigned_url")
    public ResponseEntity<ApiResponse<PresignedUrlResponseDto>> getPresignedUrl(
            @RequestBody @Valid PresignedUrlRequestDto request,
            @RequestParam Long userId) {
        PresignedUrlResponseDto response = minioStorageService.generateUploadUrl(
                request.getFileType(), request.getRoomId(), userId);
        return ResponseEntity.status(HttpStatus.OK)
                .body(ApiResponse.success(HttpStatus.OK, "업로드 URL 발급 완료", response));
    }
}
