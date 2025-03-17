package com.example.ddo_pay.pay.dto;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class BalanceChargeRequest {
    private String accoutnBank;
    private String accountAccount;
    private int amount;
    private String payPassword;
}
