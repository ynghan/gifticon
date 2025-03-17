package com.example.ddo_pay.gift.dto.select;

import lombok.Data;

@Data
public class GiftDetailResponseDto {

    private String giftTitle;
    private int amount;
    private String phoneNum;
    private String message;
    private String image;
    private String period;
    private Boolean isUsed;
    private int restaurantId;
    private String restaurantName;

}
