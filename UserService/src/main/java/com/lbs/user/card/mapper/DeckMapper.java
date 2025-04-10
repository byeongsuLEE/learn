package com.lbs.user.card.mapper;

import com.lbs.user.card.domain.Deck;
import com.lbs.user.card.dto.request.CreateCardRequestDto;
import com.lbs.user.card.dto.request.CreateDeckRequestDto;
import com.lbs.user.card.dto.response.DeckResponseDto;
import com.lbs.user.card.infrastructure.entity.DeckEntity;
import com.lbs.user.user.domain.User;
import com.lbs.user.user.dto.request.UserJoinRequestDto;
import com.lbs.user.user.dto.response.UserResponseDto;
import com.lbs.user.user.infrastructure.entity.UserEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.time.LocalDateTime;

/**
 *
 * @ 작성자   : 이병수
 * @ 작성일   : 2025-03-12
 * @ 설명     : dto , domain, orm mapper class
 * default = dto -> domain
 *
 */
@Mapper(componentModel = "spring")
public interface DeckMapper {


    //Request DTO -> Domain
//    Deck createDtoToDomain(CreateDeckRequestDto createDeckRequestDto);

    //Request DTO -> Domain
    default Deck createDtoToDomain(CreateDeckRequestDto dto){
        return Deck.createDeck(
                dto.getTitle(),
                dto.getDesc(),
                dto.getCategory(),
                dto.getTag()
        );
    }

    //Domain -> Entity
    DeckEntity domainToEntity(Deck deck);

//    //Entity -> Domain (이게 필요할까??)
//    Deck entityToDomain(DeckEntity deckEntity);

    //Entity -> ResponseDto
    DeckResponseDto entityToResponseDto(DeckEntity deckEntity);

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
