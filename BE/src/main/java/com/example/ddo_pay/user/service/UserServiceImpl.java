package com.example.ddo_pay.user.service;

import java.util.Optional;

import com.example.ddo_pay.common.exception.CustomException;
import com.example.ddo_pay.common.response.ResponseCode;
import com.example.ddo_pay.user.service.impl.UserRepository;
import org.springframework.stereotype.Service;

import com.example.ddo_pay.user.dto.UserDto;
import com.example.ddo_pay.user.dto.request.SocialLoginRequestDto;
import com.example.ddo_pay.user.dto.response.SocialLoginResponseDto;
import com.example.ddo_pay.user.entity.User;
import com.example.ddo_pay.user.mapper.UserMapper;
import com.example.ddo_pay.user.repo.UserRepo;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepo userRepo;
    private final UserMapper userMapper;
    private final UserRepository userRepository;

    @Override
    public SocialLoginResponseDto socialUserLogin(SocialLoginRequestDto reqDto) {
        // DB에서 유저 검색
        Optional<User> userDto = userRepo.findByLoginId(reqDto.getSocialId());

        // 유저 검색되지 않음 유저와 비교
        if (userDto.isEmpty()) {
        } else {
            log.debug("userDto is finded");
        }

        // response 생성
        SocialLoginResponseDto respDto = new SocialLoginResponseDto();
        respDto.setAccessToken("acc-tkn");
        respDto.setRefreshToken("ref-tkn");

        return respDto;
    }

    @Override
    public UserDto getUserInfo(UserDto reqDto) {
        Optional<User> targetUser = userRepo.findById(reqDto.getUserId());
        if (targetUser.isEmpty()) {
            log.info("Fail GetUser. No UserId in DB");
        }

        UserDto respDto = userMapper.fromUserEntity(targetUser.get());

        return respDto;
    }

    @Override
    public void changeUserInfo(UserDto reqDto) {
        Optional<User> targetUser = userRepo.findById(reqDto.getUserId());
        if (targetUser.isEmpty()) {
            log.info("Fail GetUser. No UserId in DB");
        }

        User user = targetUser.get();
        user.changePrivateInfo(reqDto);
        userRepo.save(user);
    }

    @Override
    public void logoutUser(UserDto reqDto) {
        // logout logic

    }

    @Override
    public void updateUserPhoneNumber(Long userId, String phoneNumber) {
        // 1. 전체 DB에서 해당 전화번호를 사용하는 사용자가 있는지 확인합니다.
        Optional<User> duplicateUser = userRepository.findByPhoneNum(phoneNumber);
        // 만약 해당 전화번호를 가진 사용자가 존재하고, 그것이 현재 업데이트하려는 사용자와 다르다면 중복으로 처리합니다.
        if (duplicateUser.isPresent() && !duplicateUser.get().getId().equals(userId)) {
            throw new CustomException(ResponseCode.ALREADY_EXIST_PHONE);
            // ResponseCode.ALREADY_EXIST_PHONE는 "이미 있는 번호입니다" 같은 메시지와 적절한 HTTP 상태 코드(예: 409 Conflict)를 정의한 enum 값이어야 합니다.
        }
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(ResponseCode.NO_EXIST_USER));
        // UserDto에 phoneNum 세팅
        UserDto dto = UserDto.builder()
                .phoneNum(phoneNumber)
                .build();

        // 엔티티의 메서드를 통해 phoneNum 변경
        user.changePrivateInfo(dto);

        userRepository.save(user);
    }
}
