package com.example.ddo_pay.gift.dto;

import com.example.ddo_pay.gift.entity.USED;
import lombok.Data;

@Data
public class GiftSelectResponseDto {
    private int giftId;
    private String sendUserName;
    private String period;
    private String image;
    private USED isUsed;
}
