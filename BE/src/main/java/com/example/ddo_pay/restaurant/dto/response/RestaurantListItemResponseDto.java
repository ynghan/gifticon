package com.example.ddo_pay.restaurant.dto.response;

import lombok.Data;

/**
 * 등록 맛집 리스트 조회 시 반환되는 맛집 정보 DTO
 */
@Data
public class RestaurantListItemResponseDto {

	private String restaurantName;
	private String restaurantImage;  // URL
	private String address;
	private Double latitude;
	private Double longitude;
	private int visitedCount;
}
