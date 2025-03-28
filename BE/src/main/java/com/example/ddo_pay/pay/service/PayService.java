package com.example.ddo_pay.pay.service;


import com.example.ddo_pay.pay.dto.request.AccountVerifyRequest;
import com.example.ddo_pay.pay.dto.request.RegisterAccountRequest;
import com.example.ddo_pay.pay.dto.request.RegisterPasswordRequest;

import org.springframework.stereotype.Service;

@Service
public interface PayService {


    // 계좌 유효 확인 로직
    String verifyAccount(Long userId, AccountVerifyRequest request);

    // 본인 계좌 인증 후 계좌 등록
    void registerAccount(Long userId, RegisterAccountRequest request);

    // 비밀번호 등록 및 또페이 생성
    void registerPayPassword(Long userId, RegisterPasswordRequest request);

}
