package com.example.ddo_pay.gift.service.impl;

import com.example.ddo_pay.common.exception.CustomException;
import com.example.ddo_pay.common.response.ResponseCode;
import com.example.ddo_pay.gift.dto.GiftCheckResponseDto;
import com.example.ddo_pay.gift.dto.GiftSelectResponseDto;
import com.example.ddo_pay.gift.dto.create.GiftCreateRequestDto;
import com.example.ddo_pay.gift.dto.select.GiftCheckRequestDto;
import com.example.ddo_pay.gift.dto.select.GiftDetailResponseDto;
import com.example.ddo_pay.gift.dto.update.GiftUpdateRequestDto;
import com.example.ddo_pay.gift.entity.Gift;
import com.example.ddo_pay.gift.entity.GiftBox;
import com.example.ddo_pay.gift.entity.USED;
import com.example.ddo_pay.gift.repository.GiftBoxRepository;
import com.example.ddo_pay.gift.repository.GiftRepository;
import com.example.ddo_pay.gift.service.GiftService;
import com.example.ddo_pay.pay.service.PayService;
import com.example.ddo_pay.restaurant.entity.Restaurant;
import com.example.ddo_pay.restaurant.repository.RestaurantRepository;
import com.example.ddo_pay.user.entity.User;
import com.example.ddo_pay.user.service.impl.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class GiftServiceImpl implements GiftService {

    private final GiftRepository giftRepository;
    private final GiftBoxRepository giftBoxRepository;
    private final UserRepository userRepository;
    private final RestaurantRepository restaurantRepository;

    private final PayService payService;


    @Override
    public void create(GiftCreateRequestDto dto, Integer userId) {

        // 1. 맛집의 메뉴들의 정보와 사용자 커스텀 정보를 받아서 DB에 저장한다.

        User user = userRepository.findById(Long.valueOf(userId)).orElseThrow(() -> new CustomException(ResponseCode.NO_EXIST_USER));
        Restaurant restaurant = restaurantRepository.findById(dto.getRestaurant().getId()).orElseThrow(() -> new CustomException(ResponseCode.NO_EXIST_RESTAURANT));

        String menuComb = dto.getRestaurant().getMenuDtoList().stream()
                .map(menu -> menu.getMenuName() + "(" + menu.getMenuCount() + "개)")
                .collect(Collectors.joining(", "));



        Gift gift = Gift.builder()
                .title(dto.getGiftTitle())
                .amount(dto.getAmount())
                .message(dto.getMessage())
                .image(dto.getImage())
                .phoneNum(dto.getPhoneNum())
                .menuCombination(menuComb)
                .user(user)
                .restaurant(restaurant)
                .usedStatus(USED.BEFORE_USE)
                .period(LocalDateTime.now().plusMonths(3))
                .build();

        // 기프티콘 저장
        giftRepository.save(gift);

        // 2. 저장된 기프티콘에 대해 받은 기프티콘 목록에 추가하기
        GiftBox giftBox = new GiftBox(user, gift);
        giftBoxRepository.save(giftBox);

        gift.setGiftBox(giftBox);
        giftRepository.save(gift);


        // 3. 맛집 메뉴들의 총액을 계산 후, 결제자의 또페이 잔고에서 출금한다.
        int totalMenuAmount = dto.getRestaurant().getMenuDtoList().stream()
                .mapToInt(menu -> menu.getMenuAmount() * menu.getMenuCount())
                .sum();

        // 해당 유저의 또페이 계정의 잔고에서 출금되는 로직이라고 가정.
//        payService.Withdrawal(user, totalMenuAmount);


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
