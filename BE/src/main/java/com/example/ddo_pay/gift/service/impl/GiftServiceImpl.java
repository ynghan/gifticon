package com.example.ddo_pay.gift.service.impl;

import com.example.ddo_pay.gift.dto.GiftCheckResponseDto;
import com.example.ddo_pay.gift.dto.GiftSelectResponseDto;
import com.example.ddo_pay.gift.dto.create.GiftCreateRequestDto;
import com.example.ddo_pay.gift.dto.select.GiftCheckRequestDto;
import com.example.ddo_pay.gift.dto.select.GiftDetailResponseDto;
import com.example.ddo_pay.gift.dto.update.GiftUpdateRequestDto;
import com.example.ddo_pay.gift.entity.Gift;
import com.example.ddo_pay.gift.repository.GiftRepository;
import com.example.ddo_pay.gift.service.GiftService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class GiftServiceImpl implements GiftService {

    private final GiftRepository giftRepository;

    @Override
    public void create(GiftCreateRequestDto dto) {
        Gift gift = Gift.builder()
                .title(dto.getGiftTitle())
                .amount(dto.getAmount())
                .phoneNum(dto.getPhoneNum())
                .message(dto.getMessage())
                .image(dto.getImage())
                .build();

        giftRepository.save(gift);
    }


    @Override
    public void assignment(GiftUpdateRequestDto dto) {

    }

    @Override
    public List<GiftSelectResponseDto> selectMyList() {
        return null;
    }

    @Override
    public GiftDetailResponseDto selectDetail(int giftId) {
        return null;
    }

    @Override
    public GiftCheckResponseDto usedCheck(GiftCheckRequestDto dto) {
        return null;
    }
}
