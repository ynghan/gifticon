package com.example.ddo_pay.common.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum ResponseCode {


    SUCCESS_SOCIAL_LOGIN(200, HttpStatus.OK, "인가 코드를 통해 액세스 토큰 요청이 성공했습니다."),
    SUCCESS_SOCIAL_LOGIN1(200, HttpStatus.OK, "인가 코드를 통해 액세스 토큰 요청이 성공했습니다."),
    SUCCESS_SOCIAL_LOGIN2(200, HttpStatus.OK, "인가 코드를 통해 액세스 토큰 요청이 성공했습니다."),
    SUCCESS_LOGIN(successCode(), HttpStatus.NO_CONTENT, "로그인이 성공적으로 완료되었습니다.");


    private int code;
    private HttpStatus httpStatus;
    private String message;

    private static int successCode() {
        return 200;
    }
}
