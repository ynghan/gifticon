package com.example.ddo_pay.restaurant.dto.response;

import lombok.Data;

/**
 * 등록 맛집 리스트 조회 시 반환되는 맛집 정보 DTO
 */
@Data
public class RestaurantListItemResponseDto {
	private Long id;
	private String placeName ;
	private String restaurantImage;  // URL
	private String addressName ;
	private ResponsePositionDto position;
	private int visitedCount;
}
