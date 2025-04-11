package com.lbs.user.card.controller;

import com.lbs.user.card.domain.Deck;
import com.lbs.user.card.dto.request.CreateDeckRequestDto;
import com.lbs.user.card.dto.response.DeckResponseDto;
import com.lbs.user.card.mapper.DeckMapper;
import com.lbs.user.card.service.DeckService;
import com.lbs.user.common.response.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 작성자  : lbs
 * 날짜    : 2025-04-10
 * 풀이방법
 **/


@RequestMapping("/deck")
@RestController
@RequiredArgsConstructor
public class DeckController {
    private final DeckService deckService;
    private final DeckMapper deckMapper;
    @PostMapping("/create")
    public ResponseEntity<ApiResponse<DeckResponseDto>> createDeck(@RequestBody CreateDeckRequestDto deckRequestDto ) {
        Deck deck = deckMapper.createDtoToDomain(deckRequestDto);
        Deck savedDeck = deckService.createDeck(deck);
        System.out.println(deck.getAuditInfo());
        DeckResponseDto deckResponseDto = deckMapper.domainToResponseDto(savedDeck);

        // srp 원칙을 지키기 위해 생성된 정보는 id를 통해서 다시 조회

        // deckService.findByid(deckId)

        return ResponseEntity.status(HttpStatus.OK)
                .body(ApiResponse.success(HttpStatus.OK,"성공했습니다", deckResponseDto));

    }
}
