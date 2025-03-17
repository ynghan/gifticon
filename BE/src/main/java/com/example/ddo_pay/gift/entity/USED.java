package com.example.ddo_pay.gift.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum USED {
    BEFORE_USE(1), AFTER_USE(2), EXPIRED(3);

    private int state;
}
