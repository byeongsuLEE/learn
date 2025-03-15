package com.lbs.user.common.mapper;

import com.lbs.user.domain.User;
import com.lbs.user.infrastructure.entity.UserEntity;
import com.lbs.user.dto.request.UserJoinRequestDto;
import org.mapstruct.Mapper;

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
    User userJoinDtoToDomain(UserJoinRequestDto userJoinDto);

    //Domain -> dto


    // user -> entity
    UserEntity userToEntity(User user);

    // entity -> user
    User userEntityToUser (UserEntity userEntity);

}
