package com.example.ddo_pay.gift.dto.select;

import lombok.Data;

@Data
public class GiftCheckRequestDto {
    private String lat;
    private String lng;
    private int id;
    private String password;
}
