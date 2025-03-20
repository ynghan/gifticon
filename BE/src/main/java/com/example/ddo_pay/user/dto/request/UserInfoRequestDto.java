package com.example.ddo_pay.user.dto.request;

import lombok.Data;

@Data
public class UserInfoRequestDto {
    private String userEmail;
    private String phoneNum;
    private String birth;
}
