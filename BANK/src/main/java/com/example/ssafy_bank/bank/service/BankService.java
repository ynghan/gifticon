package com.example.ssafy_bank.bank.service;


import com.example.ssafy_bank.bank.dto.response.LoginResponseDto;

public interface BankService {

    void createUserKey(String email);

    LoginResponseDto login(String email);

}
