package com.example.ddo_pay.user.mapper;

import com.example.ddo_pay.user.dto.UserDto;
import com.example.ddo_pay.user.dto.request.UserInfoRequestDto;
import com.example.ddo_pay.user.dto.response.UserInfoResponseDto;
import com.example.ddo_pay.user.entity.User;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2025-04-10T12:01:51+0900",
    comments = "version: 1.5.3.Final, compiler: javac, environment: Java 17.0.14 (Amazon.com Inc.)"
)
@Component
public class UserMapperImpl implements UserMapper {

    @Override
    public UserInfoResponseDto toUserInfoResponseDto(UserDto userDto) {
        if ( userDto == null ) {
            return null;
        }

        UserInfoResponseDto userInfoResponseDto = new UserInfoResponseDto();

        userInfoResponseDto.setUserName( userDto.getName() );
        userInfoResponseDto.setEmail( userDto.getEmail() );
        userInfoResponseDto.setPhoneNum( userDto.getPhoneNum() );
        userInfoResponseDto.setBirth( userDto.getBirth() );

        return userInfoResponseDto;
    }

    @Override
    public UserDto fromUserInfoRequestDto(UserInfoRequestDto userInfoRequestDto) {
        if ( userInfoRequestDto == null ) {
            return null;
        }

        UserDto.UserDtoBuilder userDto = UserDto.builder();

        userDto.email( userInfoRequestDto.getUserEmail() );
        userDto.userId( userInfoRequestDto.getUserId() );
        userDto.phoneNum( userInfoRequestDto.getPhoneNum() );
        userDto.birth( userInfoRequestDto.getBirth() );

        return userDto.build();
    }

    @Override
    public UserDto fromUserEntity(User user) {
        if ( user == null ) {
            return null;
        }

        UserDto.UserDtoBuilder userDto = UserDto.builder();

        if ( user.getId() != null ) {
            userDto.userId( user.getId() );
        }
        userDto.birth( mapLocalDateTimeToString( user.getBirthday() ) );
        userDto.loginId( user.getLoginId() );
        userDto.password( user.getPassword() );
        userDto.refreshToken( user.getRefreshToken() );
        userDto.name( user.getName() );
        userDto.email( user.getEmail() );
        userDto.phoneNum( user.getPhoneNum() );

        return userDto.build();
    }

    @Override
    public User toUserEntity(UserDto userDto) {
        if ( userDto == null ) {
            return null;
        }

        User.UserBuilder user = User.builder();

        user.id( userDto.getUserId() );
        user.birthday( mapStringToLocalDateTime( userDto.getBirth() ) );
        user.loginId( userDto.getLoginId() );
        user.password( userDto.getPassword() );
        user.name( userDto.getName() );
        user.email( userDto.getEmail() );
        user.phoneNum( userDto.getPhoneNum() );
        user.refreshToken( userDto.getRefreshToken() );

        return user.build();
    }
}
