package com.lbs.user.card.mapper;

import com.lbs.user.card.domain.Card;
import com.lbs.user.card.dto.request.CreateCardRequestDto;
import com.lbs.user.card.dto.response.CardResponseDto;
import com.lbs.user.card.infrastructure.entity.CardEntity;
import com.lbs.user.common.mapper.AuditInfoMapper;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;


@Mapper(componentModel = "spring", uses = {AuditInfoMapper.class})
public interface CardMapper {

    //request dto -> domain
    default Card createDtoDomain(CreateCardRequestDto dto) {
        return Card.createCard(dto.getDeckId(), dto.getTitle(), dto.getDesc());
    }

    //domain -> entity
    @Mapping(source = "desc", target = "description")
    CardEntity domainToEntity(Card dto);

    //entity -> domain
    default Card entityToDomain(CardEntity cardEntity) {
        return Card.createCard(cardEntity.getId(), cardEntity.getDeck().getId(),
                cardEntity.getTitle(), cardEntity.getDescription(), AuditInfoMapper.entityToAuditInfoStatic(cardEntity));
    }

    //entity -> dto
    @Named("entityToResponseDto")
    @Mapping(source = ".", target = "auditInfo", qualifiedByName = "entityToAuditInfo")
    // CardEntity의 deck 필드에 있는 id를 CardResponseDto의 deckId로 매핑합니다.
    @Mapping(source = "deck.id", target = "deckId")
    // CardEntity의 description 필드를 CardResponseDto의 desc로 매핑합니다.
    @Mapping(source = "description", target = "desc")
    CardResponseDto entityToResponseDto(CardEntity cardEntity);

    //entity -> dto
    static CardResponseDto entityToResponseDtoStatic(CardEntity cardEntity) {
        return CardResponseDto.builder().
                id(cardEntity.getId())
                .deckId(cardEntity.getDeck().getId())
                .title(cardEntity.getTitle())
                .desc(cardEntity.getDescription())
                .auditInfo(AuditInfoMapper.entityToAuditInfoStatic(cardEntity))
                .build();
    }


    //domain -> dto
    public CardResponseDto domainToResponseDto(Card card);


    static Card entityToDomainStatic(CardEntity cardEntity) {
        return Card.createCard(cardEntity.getId(),cardEntity.getDeck().getId(),cardEntity.getTitle(), cardEntity.getDescription(), AuditInfoMapper.entityToAuditInfoStatic(cardEntity));
    }
}
