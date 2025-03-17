package com.example.ddo_pay.common.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum ResponseCode {

    // 기프티콘
    SUCCESS_CREATE_GIFTICON(201, HttpStatus.CREATED, "기프티콘이 성공적으로 생성되었습니다."),
    SUCCESS_ASSIGNMENT_GIFTICON(200, HttpStatus.OK, "기프티콘이 성공적으로 양도되었습니다."),
    SUCCESS_LIST_GIFTICON(200, HttpStatus.OK, "받은 기프티콘 리스트가 조회되었습니다."),
    SUCCESS_DETAIL_GIFTICON(200, HttpStatus.OK, "기프티콘 상세 정보가 조회되었습니다."),
    SUCCESS_CHECK_GIFTICON(200, HttpStatus.OK, "기프티콘 사용여부가 조회되었습니다.");

    private int code;
    private HttpStatus httpStatus;
    private String message;

    private static int successCode() {
        return 200;
    }
}
