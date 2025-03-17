package com.example.ddo_pay.restaurant.dto.request;

import lombok.Data;

import java.util.List;

/**
 * 맛집 등록 시, 클라이언트가 전달하는 Request DTO
 */
@Data
public class RestaurantCreateRequestDto {

	private String restaurantName;
	private String restaurantImage;  // URL
	private String address;
	private Double latitude;
	private Double longitude;

	// 메뉴 목록
	private List<RestaurantMenuRequestDto> menu;

	// 기본값 0
	private int visitedCount;
}
