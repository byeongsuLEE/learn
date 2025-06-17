package com.lbs.user.card.controller;

import com.lbs.user.card.domain.Card;
import com.lbs.user.card.domain.Deck;
import com.lbs.user.card.dto.request.UpdateDeckRequestDto;
import com.lbs.user.card.dto.request.CreateCardRequestDto;
import com.lbs.user.card.dto.request.CreateDeckRequestDto;
import com.lbs.user.card.dto.response.CardResponseDto;
import com.lbs.user.card.dto.response.DeckResponseDto;
import com.lbs.user.card.infrastructure.repository.DeckRepository;
import com.lbs.user.card.mapper.CardMapper;
import com.lbs.user.card.mapper.DeckMapper;
import com.lbs.user.card.service.DeckService;
import com.lbs.user.common.response.ApiResponse;
import com.lbs.user.common.response.ErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * 작성자  : lbs
 * 날짜    : 2025-04-10
 * 풀이방법
 **/


@RequestMapping("/deck")
@RestController
@RequiredArgsConstructor
@Slf4j
public class DeckController {
    private final DeckService deckService;
    private final DeckMapper deckMapper;
    private final CardMapper cardMapper;
    private final DeckRepository  deckRepository;
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
//    //페이징 처리 안한 deck 조회
//    @GetMapping
//    public ResponseEntity<ApiResponse<List<DeckResponseDto>>> getAllDecks() {
//        List<Deck> decks = deckService.readAllDecks();
//        List<DeckResponseDto> deckResponseDtoList = decks.stream().map(deckMapper::domainToResponseDto).toList();
//        return ResponseEntity.status(HttpStatus.OK)
//                .body(ApiResponse.success(HttpStatus.OK, deckResponseDtoList));
//    }

    @GetMapping
        public ResponseEntity<ApiResponse<Slice<DeckResponseDto>>> listDecks(Pageable pageable) {
        Slice<DeckResponseDto> deckResponseDtoList = deckRepository.findByAll(pageable);
        return ResponseEntity.status(HttpStatus.OK)
                .body(ApiResponse.success(HttpStatus.OK, "list 페이징 처리 성공",deckResponseDtoList));

        // 의문 1 . deckService를 갈필요가있나? 그냥바로 repository로 가면되잖아?
//        PageRequest pageRequest = PageRequest.of(0, 10, Sort.by(Sort.Direction.DESC , "id"));
//        deckService.readPageDecks(pageRequest); // 조건 ,
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<DeckResponseDto>> getDeck(@PathVariable("id") Long id) {

        Deck byId = deckRepository.findById(id);
        Deck deck = deckService.readDeck(id);
        DeckResponseDto deckResponseDto = deckMapper.domainToResponseDto(deck);
        return ResponseEntity.status(HttpStatus.OK)
                .body(ApiResponse.success(HttpStatus.OK, deckResponseDto));
    }

    @PatchMapping ("/update")
    public ResponseEntity<ApiResponse<DeckResponseDto>> updateDeck(@RequestBody UpdateDeckRequestDto deckRequestDto){
        Deck deck = deckMapper.updateDtoToDomain(deckRequestDto);
        Deck updateDeck = deckService.updateDeck(deck);
        DeckResponseDto deckResponseDto = deckMapper.domainToResponseDto(updateDeck);
        return ResponseEntity.status(HttpStatus.OK)
                .body(ApiResponse.success(HttpStatus.OK,deckResponseDto));
    }

    @DeleteMapping ("/{id}/delete")
    public  ResponseEntity<ApiResponse<Long>> deleteDeck(@PathVariable("id") Long id){
        deckService.deleteDeck(id);

        return ResponseEntity.status(HttpStatus.OK)
                .body(ApiResponse.success(HttpStatus.OK,id + "제거가 완료되었습니다",id));
    }


    @DeleteMapping("/{id}/delete/{card_id}")
    public  ResponseEntity<ApiResponse<Long>> deleteCard(@PathVariable("id") Long id, @PathVariable("card_id") Long card_id){
        Long deleteCardId = deckService.deleteCard(id, card_id);

        return ResponseEntity.status(HttpStatus.OK)
                .body(ApiResponse.success(HttpStatus.OK,deleteCardId + "제거가 완료되었습니다",deleteCardId));
    }

    // card : test3
    @PostMapping("/{deck_id}/cards")
    public ResponseEntity<ApiResponse<CardResponseDto>> createCard(@PathVariable("deck_id") Long id , @RequestBody CreateCardRequestDto cardRequestDto){
        cardRequestDto.setDeckId(id);
        Card card = cardMapper.createDtoDomain(cardRequestDto);

        Card savedCard= deckService.createCard(card);
        CardResponseDto cardResponseDto = cardMapper.domainToResponseDto(savedCard);
        return ResponseEntity.status(HttpStatus.OK)
                .body(ApiResponse.success(HttpStatus.OK,"card 등록 완료했습니다." , cardResponseDto));
    }

    @PostMapping("/{id}/transaction-rollback-test-with-error")
    public ResponseEntity<ApiResponse<String>> transactionRollbackTestWithError(
            @PathVariable("id") Long id,
            @RequestBody Map<String, Object> requestBody) {

        try {
            // 1. 초기 상태 기록
            Deck deck = deckRepository.findById(id);
            int originalCount = deck.getCardCount();

            // 2. 데이터 변경

            // Redis 키 설정
            String redisKey = "test:deck:" + id;

            // 3. 트랜잭션 내에서 실행 (의도적인 에러 발생)
            deckService.updateCardCountWithTransactionTest(id, 5555, true);

            // 이 코드는 실행되지 않음 (예외 발생으로 인해)
            return ResponseEntity.status(HttpStatus.OK)
                    .body(ApiResponse.success(HttpStatus.OK, "트랜잭션 성공 (실행되지 않음)"));

        } catch (Exception e) {
            log.info("에러 메시지는 : " + e.getMessage());
            System.out.println();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error(HttpStatus.INTERNAL_SERVER_ERROR,
                            "트랜잭션 롤백 발생", ErrorCode.DECK_NOT_FOUND));
        }
    }
}

