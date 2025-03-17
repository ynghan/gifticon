package com.example.ddo_pay.gift.controller;

import com.example.ddo_pay.common.response.Response;
import com.example.ddo_pay.gift.dto.create.GiftCreateRequestDto;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static com.example.ddo_pay.common.response.ResponseCode.SUCCESS_LOGIN;


@RestController
@RequestMapping("/api/gift")
public class GiftController {


    @PostMapping
    public ResponseEntity<?> create(@RequestBody GiftCreateRequestDto dto) {
        return new ResponseEntity<>(Response.create(SUCCESS_LOGIN, dto), SUCCESS_LOGIN.getHttpStatus());
    }

}
