package com.lbs.user.card.controller;

import com.lbs.user.card.dto.request.CreateCardRequestDto;
import com.lbs.user.card.dto.response.CardResponseDto;
import com.lbs.user.card.service.CardService;
import com.lbs.user.common.response.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 작성자  : lbs
 * 날짜    : 2025-04-09
 * 풀이방법
 **/


@RestController
@RequestMapping("/card")
@RequiredArgsConstructor


public class CardController {

    @PostMapping
    public ResponseEntity<ApiResponse<CardResponseDto>> createCard (CreateCardRequestDto createCardRequestDto){

        return ResponseEntity.status(HttpStatus.OK)
                .body(ApiResponse.success(HttpStatus.OK,"카드 만들기 성공", null));
    }
}
