package com.lbs.user.card.mapper;

import com.lbs.user.card.domain.AuditInfo;
import com.lbs.user.card.domain.Card;
import com.lbs.user.card.dto.request.CreateCardRequestDto;
import com.lbs.user.card.dto.response.CardResponseDto;
import com.lbs.user.card.infrastructure.entity.CardEntity;
import com.lbs.user.common.mapper.AuditInfoMapper;
import org.hibernate.annotations.Source;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;


@Mapper(componentModel = "spring")
public interface CardMapper
{

    //request dto -> domain
    default Card createDtoDomain(CreateCardRequestDto dto){
        return Card.createCard(dto.getDeckId(), dto.getTitle(),dto.getDesc());
    }

    //domain -> entity
    @Mapping(source = "desc" , target = "description")
    CardEntity domainToEntity(Card dto);

    //entity -> domain
    default Card entityToDomain(CardEntity cardEntity){
        return Card.createCard(cardEntity.getId(), cardEntity.getDeck().getId(),
                cardEntity.getTitle(), cardEntity.getDescription(), AuditInfoMapper.entityToAuditInfo(cardEntity));
    }

    //domain -> dto
    CardResponseDto domainToResponseDto(Card card);
}
