package com.example.ddo_pay.restaurant.dto.request;

import lombok.Data;

import java.util.List;

import com.example.ddo_pay.restaurant.dto.response.ResponsePositionDto;

/**
 * 맛집 등록 시, 클라이언트가 전달하는 Request DTO
 */
@Data
public class RestaurantCreateRequestDto {
	private int userId;
	private String placeName;
	private String restaurantImage;  // URL
	private String addressName;
	private RequestPositionDto position;

	// 메뉴 목록
	private List<RestaurantMenuRequestDto> menu;

	// 기본값 0
	private int visitedCount;
}
