package com.example.ddo_pay.pay.dto.bank_request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;

@Getter
@Builder
public class TokenEqualResponseDto {
    private Boolean result;
    @JsonProperty("paymentToken")
    private String paymentToken;
    @JsonProperty("paymentAmount")
    private Integer paymentAmount;
    @JsonProperty("storeAccount")
    private String storeAccount;
    @JsonProperty("userId")
    private Long userId;
    @JsonProperty("giftId")
    private Long giftId;
}
