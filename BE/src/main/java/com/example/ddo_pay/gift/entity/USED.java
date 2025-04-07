package com.example.ddo_pay.gift.entity;

import lombok.Getter;

@Getter
public enum USED {
    BEFORE_USE,
    AFTER_USE,
    EXPIRED,
    CANCLE;

    public boolean isRefundable() {
        return this == BEFORE_USE;
    }
}
