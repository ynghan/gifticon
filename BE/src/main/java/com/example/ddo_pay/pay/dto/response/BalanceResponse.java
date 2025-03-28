package com.example.ddo_pay.pay.dto.response;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class BalanceResponse {

    @JsonIgnore
    private Long userId;
    private int payBalance;

    public BalanceResponse(int balance) {
        this.payBalance = balance;
    }
}
