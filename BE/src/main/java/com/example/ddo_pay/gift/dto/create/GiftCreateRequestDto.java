package com.example.ddo_pay.gift.dto.create;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Data
public class GiftCreateRequestDto {

    // 기프티콘 커스텀 DATA
    private String title;
    private int amount;
    private String message;
    private MultipartFile image;

    private String menuName;

    // 대상자의 phoneNum
    private String phoneNum;

    // 담을 맛집의 메뉴들에 대한 정보
    private int resId;

    private Position position;

    @Data
    private static class Position {
        private double lat;
        private double lng;
    }
}
