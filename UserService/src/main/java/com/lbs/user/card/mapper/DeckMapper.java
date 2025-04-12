package com.lbs.user.card.mapper;

import com.lbs.user.card.domain.AuditInfo;
import com.lbs.user.card.domain.Deck;
import com.lbs.user.card.dto.request.CreateCardRequestDto;
import com.lbs.user.card.dto.request.CreateDeckRequestDto;
import com.lbs.user.card.dto.request.DeckRequestDto;
import com.lbs.user.card.dto.response.DeckResponseDto;
import com.lbs.user.card.infrastructure.entity.DeckEntity;
import com.lbs.user.common.mapper.AuditInfoMapper;
import com.lbs.user.user.domain.User;
import com.lbs.user.user.dto.request.UserJoinRequestDto;
import com.lbs.user.user.dto.response.UserResponseDto;
import com.lbs.user.user.infrastructure.entity.UserEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.time.LocalDateTime;

/**
 * @ 작성자   : 이병수
 * @ 작성일   : 2025-03-12
 * @ 설명     : dto , domain, orm mapper class
 * default = dto -> domain
 */
@Mapper(componentModel = "spring")
public interface DeckMapper {

    //Request DTO -> Domain
    default Deck createDtoToDomain(CreateDeckRequestDto dto) {
        return Deck.createDeck(
                dto.getTitle(),
                dto.getDesc(),
                dto.getCategory(),
                dto.getTag()
        );
    }

   default Deck updateDtoToDomain(DeckRequestDto dto){
        return Deck.createDeck(
                dto.getId(),
                dto.getTitle(),
                dto.getDesc(),
                dto.getCategory(),
                dto.getTag(),
                dto.getAuditInfo()
        );
   }


    //Domain -> Entity
    @Mapping(source = "desc" , target = "description")
    DeckEntity domainToEntity(Deck deck);

    //Entity -> Domain (이게 필요할까??) 
    //필요한 이유 : srp 원칙을 지키기 위해서 , 서비스는 서비스의 결과값만 , 컨트롤러는 mapping 역할을 하기 위해서
    // entity -> domain이 필요하다고 판단했다.
    // entity 들어가야지만 생성되는 createAt그런것들을 card로 가져와야한다.
    // @Mapping(source = ".", target = "auditInfo", qualifiedByName = "entityToAuditInfo") -> static 변수로 가져왔습니다.
    //default : @mapping이 적용되지 않는다.
    default Deck entityToDomain(DeckEntity deckEntity) {
        return Deck.createDeck(deckEntity.getId(), deckEntity.getTitle(), deckEntity.getDescription(), deckEntity.getCategory(), deckEntity.getTag(), AuditInfoMapper.entityToAuditInfo(deckEntity));
    }

    //Entity -> ResponseDto
//    DeckResponseDto entityToResponseDto(DeckEntity deckEntity);

    DeckResponseDto domainToResponseDto(Deck savedDeck);



//    @Named("entityToAuditInfo")
//    static AuditInfo entityToAuditInfo(DeckEntity deckEntity) {
//        return AuditInfo.builder()
//                .createdDate(deckEntity.getCreatedDate())
//                .createdBy(deckEntity.getCreatedBy())
//                .lastModifiedDate(deckEntity.getLastModifiedDate())
//                .lastModifiedBy(deckEntity.getLastModifiedBy())
//                .build();
//    }

    //Domain -> ResponseDto


//    //DTO -> Domain
//    @Mapping(source = "email", target = "email")
//    default  userJoinDtoToDomain(CreateCardRequestDto createCardRequestDto){
//        return
//                User.builder()
//                        .email(userJoinDto.getEmail())
//                        .joinDate(userJoinDto.getJoinDate() == null ? LocalDateTime.now() :  userJoinDto.getJoinDate())
//                        .password( userJoinDto.getPassword() )
//                        .build();
//    }


//    UserResponseDto userDomainToUserDto(User user);
//
//
//    // user -> entity
//    @Mapping(target = "id", ignore = true)
//    UserEntity userToEntity(User user);
//
//    // entity -> user
//    User userEntityToUser (UserEntity userEntity);

}
