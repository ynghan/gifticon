package com.example.ddo_pay.user.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.ddo_pay.common.response.Response;
import static com.example.ddo_pay.common.response.ResponseCode.SUCCESS_GET_USER_INFO;
import static com.example.ddo_pay.common.response.ResponseCode.SUCCESS_LOGOUT;
import static com.example.ddo_pay.common.response.ResponseCode.SUCCESS_SOCIAL_LOGIN;
import static com.example.ddo_pay.common.response.ResponseCode.SUCCESS_UPDATE_USER_INFO;
import com.example.ddo_pay.user.dto.request.SocialLoginRequestDto;
import com.example.ddo_pay.user.dto.request.UserInfoRequestDto;
import com.example.ddo_pay.user.dto.response.SocialLoginResponseDto;
import com.example.ddo_pay.user.dto.response.UserInfoResponseDto;

@RestController
@RequestMapping("/api/users")
public class UserController {

    // 소셜 로그인
    // Request - SocialLoginRequestDto
    // Response - SocialLoginResponseDto
    @PostMapping("/social/kakao/login")
    public ResponseEntity<?> getUser(@RequestBody SocialLoginRequestDto reqDto) {
        SocialLoginResponseDto socialLoginDto = new SocialLoginResponseDto();
        socialLoginDto.setAccessToken("acc-tkn");
        socialLoginDto.setRefreshToken("ref-tkn");
        return new ResponseEntity<>(Response.create(SUCCESS_SOCIAL_LOGIN, socialLoginDto),
                SUCCESS_SOCIAL_LOGIN.getHttpStatus());
    }

    // 회원 정보 불러오기
    // Request - null
    // Response - UserInfoResponseDto
    @GetMapping("/info")
    public ResponseEntity<?> getUserInfo() {
        UserInfoResponseDto infoDto = new UserInfoResponseDto();
        infoDto.setUserName("test_user");
        infoDto.setEmail("testemail@test.net");
        infoDto.setPhoneNum("010-2222-3333");
        infoDto.setBirth("2000-02-14");

        return new ResponseEntity<>(Response.create(SUCCESS_GET_USER_INFO, infoDto),
                SUCCESS_GET_USER_INFO.getHttpStatus());
    }

    // 회원 정보 수정
    // Request - UserInfoRequestDto
    // Response - null
    @PutMapping("/info")
    public ResponseEntity<?> changeUserInfo(@RequestBody UserInfoRequestDto infoDto) {
        return new ResponseEntity<>(Response.create(SUCCESS_UPDATE_USER_INFO, null),
                SUCCESS_UPDATE_USER_INFO.getHttpStatus());
    }

    // 로그아웃
    // Request - null
    // Response - null
    @GetMapping("/logout")
    public ResponseEntity<?> logoutUser() {
        return new ResponseEntity<>(Response.create(SUCCESS_LOGOUT, null),
                SUCCESS_LOGOUT.getHttpStatus());
    }
}
