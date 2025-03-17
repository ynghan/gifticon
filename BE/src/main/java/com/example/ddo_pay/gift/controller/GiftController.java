package com.example.ddo_pay.gift.controller;

import com.example.ddo_pay.common.response.Response;
import com.example.ddo_pay.gift.dto.GiftCheckResponseDto;
import com.example.ddo_pay.gift.dto.GiftSelectResponseDto;
import com.example.ddo_pay.gift.dto.create.GiftCreateRequestDto;
import com.example.ddo_pay.gift.dto.select.GiftCheckRequestDto;
import com.example.ddo_pay.gift.dto.select.GiftDetailResponseDto;
import com.example.ddo_pay.gift.dto.update.GiftUpdateRequestDto;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

import static com.example.ddo_pay.common.response.ResponseCode.*;


@RestController
@RequestMapping("/api/gift")
public class GiftController {


    @PostMapping
    public ResponseEntity<?> create(@RequestBody GiftCreateRequestDto dto) {
        return new ResponseEntity<>(Response.create(SUCCESS_CREATE_GIFTICON, null), SUCCESS_CREATE_GIFTICON.getHttpStatus());
    }

    @PutMapping
    public ResponseEntity<?> assignment(@RequestBody GiftUpdateRequestDto dto) {
        return new ResponseEntity<>(Response.create(SUCCESS_ASSIGNMENT_GIFTICON, null), SUCCESS_ASSIGNMENT_GIFTICON.getHttpStatus());
    }

    @GetMapping
    public ResponseEntity<?> selectList() {
        List<GiftSelectResponseDto> dtos = new ArrayList<>();

        for(int i = 0; i < 3; i++) {
            GiftSelectResponseDto dto = new GiftSelectResponseDto();
            dtos.add(dto);
        }

        return new ResponseEntity<>(Response.create(SUCCESS_LIST_GIFTICON, dtos), SUCCESS_LIST_GIFTICON.getHttpStatus());
    }

    @GetMapping("/detail")
    public ResponseEntity<?> selectDetail() {
        GiftDetailResponseDto dto = new GiftDetailResponseDto();
        return new ResponseEntity<>(Response.create(SUCCESS_DETAIL_GIFTICON, dto), SUCCESS_DETAIL_GIFTICON.getHttpStatus());
    }

    @PostMapping("/check")
    public ResponseEntity<?> usedCheck(@RequestBody GiftCheckRequestDto dto) {

        GiftCheckResponseDto resDto = new GiftCheckResponseDto();
        return new ResponseEntity<>(Response.create(SUCCESS_CHECK_GIFTICON, resDto), SUCCESS_CHECK_GIFTICON.getHttpStatus());
    }

}
