package com.lbs.user.friend.controller;

import com.lbs.user.common.response.ApiResponse;
import com.lbs.user.friend.domain.Friend;
import com.lbs.user.friend.dto.request.FriendRequestDto;
import com.lbs.user.friend.dto.response.FriendResponseDto;
import com.lbs.user.friend.service.FriendService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 작성자  : lbs
 * 날짜    : 2025-10-12
 * 풀이방법
 **/


@RestController
@RequestMapping("/user_friend")
@RequiredArgsConstructor
public class FriendController {

    private final FriendService friendService ;


    //나의 친구 리스트 보여주기
    @GetMapping("/{my_user_id}")
    public ResponseEntity<ApiResponse<List<FriendResponseDto>>> getFriends(@PathVariable("my_user_id") Long my_user_id) {

        List<Friend> friends = friendService.getFriends(my_user_id);
        List<FriendResponseDto> friendResponseDtoList  = friends.stream()
                .map((f) -> f.mapToResponseDto(f))
                .toList();

        return ResponseEntity.status(HttpStatus.OK)
                .body(ApiResponse.success(HttpStatus.OK,friendResponseDtoList));
    }


    /**
     *
     * @작성자   : lbs
     * @작성일   : 2025-10-13
     * @설명     : 친구 요청 보내기 (이메일 검색, 이름, 닉네임) 초기에는 email를 이용한 친구 요청
     * @param friendRequestDto
     * @return
     */
    @PostMapping("request")
    public ResponseEntity<ApiResponse<String>> friendRequest(@RequestBody FriendRequestDto friendRequestDto ){

        friendService.sendFriendRequest(friendRequestDto);

        return ResponseEntity.status(HttpStatus.OK)
                .body(ApiResponse.success(HttpStatus.OK,"친구 요청 보내기 완료"));
    }

    //

    // 친구 요청 수락  api
    // 질문
    //1. 게임에서처럼 새로고침없이 수락 시 친구가 보이도록 하는 기능은 어떤 식으로 구현할 것인가?

    @PatchMapping("request/{friend_request_id}/accept")
    public ResponseEntity<ApiResponse<FriendResponseDto>> acceptedFriendRequest(@PathVariable(value = "friend_request_id") Long friendRequestId){
        Friend friend = friendService.acceptFriendRequest(friendRequestId);

        FriendResponseDto friendResponseDto = friend.mapToResponseDto(friend);
        return ResponseEntity.status(HttpStatus.OK)
                .body(ApiResponse.success(HttpStatus.OK,friendResponseDto));

    }


    //대기 중인 친구 요청 리스트 보기





}
