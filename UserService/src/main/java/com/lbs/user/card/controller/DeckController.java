package com.lbs.user.card.controller;

import com.lbs.user.card.domain.Deck;
import com.lbs.user.card.dto.request.CreateDeckRequestDto;
import com.lbs.user.card.dto.request.DeckRequestDto;
import com.lbs.user.card.dto.response.DeckResponseDto;
import com.lbs.user.card.mapper.DeckMapper;
import com.lbs.user.card.service.DeckService;
import com.lbs.user.common.response.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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
        Deck savedDeck = deckService.saveDeck(deck);
        DeckResponseDto deckResponseDto = deckMapper.domainToResponseDto(savedDeck);

        // srp 원칙을 지키기 위해 생성된 정보는 id를 통해서 다시 조회
        // deckService.findByid(deckId)
        return ResponseEntity.status(HttpStatus.OK)
                .body(ApiResponse.success(HttpStatus.OK,"성공했습니다", deckResponseDto));

    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<DeckResponseDto>>> getAllDecks() {
        List<Deck> decks = deckService.readAllDecks();
        List<DeckResponseDto> deckResponseDtoList = decks.stream().map(deckMapper::domainToResponseDto).toList();
        return ResponseEntity.status(HttpStatus.OK)
                .body(ApiResponse.success(HttpStatus.OK, deckResponseDtoList));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<DeckResponseDto>> getDeck(@PathVariable("id") Long id) {
        Deck deck = deckService.readDeck(id);
        DeckResponseDto deckResponseDto = deckMapper.domainToResponseDto(deck);
        return ResponseEntity.status(HttpStatus.OK)
                .body(ApiResponse.success(HttpStatus.OK, deckResponseDto));
    }

    @PatchMapping ("/update")
    public ResponseEntity<ApiResponse<DeckResponseDto>> updateDeck(@RequestBody DeckRequestDto deckRequestDto){
        Deck deck = deckMapper.updateDtoToDomain(deckRequestDto);
        Deck updateDeck = deckService.updateDeck(deck);
        DeckResponseDto deckResponseDto = deckMapper.domainToResponseDto(updateDeck);
        return ResponseEntity.status(HttpStatus.OK)
                .body(ApiResponse.success(HttpStatus.OK,deckResponseDto));
    }
}

