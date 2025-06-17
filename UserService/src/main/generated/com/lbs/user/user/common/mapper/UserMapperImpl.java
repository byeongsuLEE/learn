package com.lbs.user.user.common.mapper;

import com.lbs.user.user.domain.User;
import com.lbs.user.user.infrastructure.entity.UserEntity;
import java.time.LocalDateTime;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2025-03-26T22:06:29+0900",
    comments = "version: 1.5.5.Final, compiler: javac, environment: Java 17.0.13 (BellSoft)"
)
@Component
public class UserMapperImpl implements UserMapper {

    @Override
    public UserJoinResponseDto userToJoinResponseDto(User user) {
        if ( user == null ) {
            return null;
        }

        UserJoinResponseDto.UserJoinResponseDtoBuilder userJoinResponseDto = UserJoinResponseDto.builder();

        userJoinResponseDto.email( user.getEmail() );
        userJoinResponseDto.password( user.getPassword() );
        userJoinResponseDto.joinDate( user.getJoinDate() );

        return userJoinResponseDto.build();
    }

    @Override
    public UserEntity userToEntity(User user) {
        if ( user == null ) {
            return null;
        }

        String email = null;
        String password = null;
        LocalDateTime joinDate = null;

        email = user.getEmail();
        password = user.getPassword();
        joinDate = user.getJoinDate();

        Long id = null;

        UserEntity userEntity = new UserEntity( id, email, password, joinDate );

        return userEntity;
    }

    @Override
    public User userEntityToUser(UserEntity userEntity) {
        if ( userEntity == null ) {
            return null;
        }

        User.UserBuilder user = User.builder();

        user.email( userEntity.getEmail() );
        user.password( userEntity.getPassword() );
        user.joinDate( userEntity.getJoinDate() );

        return user.build();
    }
}
