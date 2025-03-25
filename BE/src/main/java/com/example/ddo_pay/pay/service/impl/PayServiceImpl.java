package com.example.ddo_pay.pay.service.impl;

import com.example.ddo_pay.pay.dto.request.BalanceChargeRequest;
import com.example.ddo_pay.pay.dto.request.RegisterAccountRequest;
import com.example.ddo_pay.pay.dto.request.RegisterPasswordRequest;
import com.example.ddo_pay.pay.dto.response.BalanceResponse;
import com.example.ddo_pay.pay.dto.response.GetAccountResponse;
import com.example.ddo_pay.pay.dto.response.GetPointResponse;
import com.example.ddo_pay.pay.service.PayService;
import org.springframework.stereotype.Service;

@Service
public class PayServiceImpl implements PayService {
    @Override
    public BalanceResponse selectBalance(int userId) {
        return null;
    }

    @Override
    public GetAccountResponse selectAccount(int userId) {
        return null;
    }

    @Override
    public GetPointResponse selectPoint(int userId) {
        return null;
    }

    @Override
    public void deleteAccount(int accountId) {

    }

    @Override
    public void creatAccount(RegisterAccountRequest dto) {

    }

    @Override
    public void refund(int giftId) {

    }

    @Override
    public void createPassword(RegisterPasswordRequest dto) {

    }

    @Override
    public void updateBalance(BalanceChargeRequest dto) {

    }
}
