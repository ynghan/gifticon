package com.example.ddo_pay.user.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class UpdatePhoneRequestDto {
    @JsonProperty("phone_num")
    private String phoneNum;
}