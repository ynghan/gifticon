package com.example.ddo_pay.pay.controller;

import com.example.ddo_pay.common.response.Response;
import com.example.ddo_pay.common.response.ResponseCode;
import com.example.ddo_pay.common.util.SecurityUtil;
import com.example.ddo_pay.pay.dto.request.AccountVerifyRequest;
import com.example.ddo_pay.pay.dto.request.RegisterAccountRequest;
import com.example.ddo_pay.pay.dto.request.RegisterPasswordRequest;
import com.example.ddo_pay.pay.service.PayService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/pay")
@RequiredArgsConstructor
public class PayController {

    private final PayService payService;

    // 유효 계좌 인증
    @PostMapping("/account/verify")
    public ResponseEntity<?> verifyAccount(@RequestBody AccountVerifyRequest request) {
        Long userId = SecurityUtil.getUserId();
        String financeCode = payService.verifyAccount(userId, request);

        ResponseCode responseCode = switch (financeCode) {
            case "H0000" -> ResponseCode.SUCCESS_VERIFY_ACCOUNT;
            case "A1003" -> ResponseCode.INVALID_ACCOUNT;
            case "ERR_API" -> ResponseCode.FINANCE_API_ERROR;
            case "ERR_PARSING" -> ResponseCode.FINANCE_PARSING_ERROR;
            default -> ResponseCode.UNKNOWN_ERROR;
        };

        return new ResponseEntity<>(Response.create(responseCode, null), responseCode.getHttpStatus());
    }

    // 본인 계좌 인증 후 계좌 연동
    @PostMapping("/account")
    public ResponseEntity<?> registerAccount(@RequestBody RegisterAccountRequest request) {
        Long userId = SecurityUtil.getUserId();
        payService.registerAccount(userId, request);

        return ResponseEntity.ok(
                Response.create(ResponseCode.SUCCESS_REGISTER_ACCOUNT, null)
        );
    }


    // 비밀번호 등록 및 또페이 생성
    @PostMapping("/password")
    public ResponseEntity<?> registerPassword(@RequestBody RegisterPasswordRequest request) {
        Long userId = SecurityUtil.getUserId();
        payService.registerPayPassword(userId, request);
        return ResponseEntity.ok(Response.create(ResponseCode.SUCCESS_REGISTER_DDOPAY, null));
    }






}
