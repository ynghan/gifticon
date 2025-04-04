package com.example.ddo_pay.pay.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class GetHistoryListResponse {

    private String title; // 결제 내역 제목
    private LocalDateTime time; // 결제 시간
    private int inOutAmount; // 입출금 내역
    private String type; // 잔고인지 포인트인지

}
