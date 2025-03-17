package com.example.ddo_pay.user.dto.response;

import lombok.Data;

@Data
public class UserInfoResponseDto {
    private String userName;
    private String email;
    private String phoneNum; // 전화번호 형식 010-1234-5678
    private String birth;
}
