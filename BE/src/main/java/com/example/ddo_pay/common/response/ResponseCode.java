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
    SUCCESS_LOGIN(successCode(), HttpStatus.OK, "로그인이 성공적으로 완료되었습니다."),

    // 페이 도메인
    SUCCESS_BALANCE_CHECK(successCode(), HttpStatus.OK, "잔액조회가 성공적으로 완료되었습니다."),
    SUCCESS_POINT_CHECK(successCode(), HttpStatus.OK, "포인트조회가 성공적으로 완료되었습니다."),
    SUCCESS_DELETE_ACCOUNT(successCode(), HttpStatus.OK, "연결 계좌 삭제가 성공적으로 완료되었습니다."),
    SUCCESS_REGISTER_ACCOUNT(successCode(), HttpStatus.OK, "새로운 계좌 등록이 성공적으로 완료되었습니다."),
    SUCCESS_REFUND_GIFT(successCode(), HttpStatus.OK, "기프티콘 환불이 성공적으로 완료되었습니다."),
    SUCCESS_REGISTER_PASSWORD(successCode(), HttpStatus.OK, "비밀번호 등록이 성공적으로 완료되었습니다."),
    SUCCESS_BALANCE_CHARGE(successCode(), HttpStatus.OK, "또페이 충전이 성공적으로 완료되었습니다."),
    SUCCESS_ACCOUNT_CHECK(successCode(), HttpStatus.OK, "계좌조회가 성공적으로 완료되었습니다.");

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
