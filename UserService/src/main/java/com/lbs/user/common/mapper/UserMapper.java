package com.lbs.user.common.mapper;

import com.lbs.user.domain.User;
import com.lbs.user.dto.response.UserJoinResponseDto;
import com.lbs.user.infrastructure.entity.UserEntity;
import com.lbs.user.dto.request.UserJoinRequestDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.time.LocalDateTime;

/**
 *
 * @ 작성자   : 이병수
 * @ 작성일   : 2025-03-12
 * @ 설명     : dto , domain, orm mapper class
 *
 */
@Mapper(componentModel = "spring")
public interface UserMapper {
    //DTO -> Domain
    @Mapping(source = "email", target = "email")
    default User userJoinDtoToDomain(UserJoinRequestDto userJoinDto){

        return User.builder()
                .email(userJoinDto.getEmail())
                .joinDate(userJoinDto.getJoinDate() == null ? LocalDateTime.now() :  userJoinDto.getJoinDate())
                .password( userJoinDto.getPassword() )
                .build();
    }

    //Domain ->response dto
    UserJoinResponseDto userToJoinResponseDto(User user);

    // user -> entity
    @Mapping(target = "id", ignore = true)
    UserEntity userToEntity(User user);

    // entity -> user
    User userEntityToUser (UserEntity userEntity);

}
