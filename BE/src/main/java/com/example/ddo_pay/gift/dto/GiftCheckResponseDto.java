package com.example.ddo_pay.gift.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class GiftCheckResponseDto {
    private int userId;
    private int amount;
    private LocalDateTime period;
    private Boolean isUsed;
}
