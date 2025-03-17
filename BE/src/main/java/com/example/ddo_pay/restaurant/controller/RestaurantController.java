package com.example.ddo_pay.restaurant.controller;

import static com.example.ddo_pay.common.response.ResponseCode.*;

import java.util.ArrayList;
import java.util.List;

import com.example.ddo_pay.common.response.Response;
import com.example.ddo_pay.common.response.ResponseCode;
import com.example.ddo_pay.restaurant.dto.request.CustomMenuRequestDto;
import com.example.ddo_pay.restaurant.dto.request.RestaurantCreateRequestDto;
import com.example.ddo_pay.restaurant.dto.request.RestaurantDeleteRequestDto;
import com.example.ddo_pay.restaurant.dto.response.RestaurantListItemResponseDto;
import com.example.ddo_pay.restaurant.dto.response.RestaurantSimpleResponseDto;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/api/restaurants")
public class RestaurantController {

	/**
	 * 맛집 등록 (POST /api/restaurants)
	 *
	 * RequestBody 예시
	 * {
	 *   "restaurantName": "김밥천국",
	 *   "restaurantImage": "http://example.com/images/abc.jpg",
	 *   "address": "서울 어딘가",
	 *   "latitude": 37.1234,
	 *   "longitude": 127.5678,
	 *   "menu": [
	 *     {
	 *       "menuName": "라면",
	 *       "menuPrice": 4000,
	 *       "menuImage": "http://example.com/images/ramen.jpg"
	 *     },
	 *     ...
	 *   ],
	 *   "visitedCount": 0
	 * }
	 *
	 * Response 예시 (status 204):
	 * {
	 *   "status": {
	 *     "code": 204,
	 *     "message": "맛집 등록 성공."
	 *   },
	 *   "content": null
	 * }
	 */
	@PostMapping
	public ResponseEntity<?> create(@RequestBody RestaurantCreateRequestDto requestDto) {
		return ResponseEntity
			.status(ResponseCode.SUCCESS_CREATE_RESTAURANT.getHttpStatus())
			.body(Response.create(ResponseCode.SUCCESS_CREATE_RESTAURANT, null));
	}

	@DeleteMapping
	public ResponseEntity<?> removeRestaurant(@RequestBody RestaurantDeleteRequestDto requestDto) {
		// 실제 삭제 로직: e.g. restaurantService.deleteRestaurant(requestDto.getRestaurantId());

		// 200 OK
		return ResponseEntity
			.status(SUCCESS_REMOVE_RESTAURANT.getHttpStatus())  // 200
			.body(Response.create(SUCCESS_REMOVE_RESTAURANT, null));
	}

	@GetMapping
	public ResponseEntity<?> getRegisteredRestaurantList() {
		List<RestaurantListItemResponseDto> dummyList = new ArrayList<>();

		RestaurantListItemResponseDto r1 = new RestaurantListItemResponseDto();
		r1.setRestaurantName("김밥천국");
		r1.setRestaurantImage("http://example.com/images/kimbab.jpg");
		r1.setAddress("서울 종로구 어딘가");
		r1.setLatitude(37.1234);
		r1.setLongitude(127.5678);
		r1.setVisitedCount(0);

		RestaurantListItemResponseDto r2 = new RestaurantListItemResponseDto();
		r2.setRestaurantName("식당B");
		r2.setRestaurantImage("http://example.com/images/restaurant_b.jpg");
		r2.setAddress("서울 강남구 어딘가");
		r2.setLatitude(37.5678);
		r2.setLongitude(127.1234);
		r2.setVisitedCount(3);

		dummyList.add(r1);
		dummyList.add(r2);

		// 공통 Response로 감싸서 반환
		return ResponseEntity
			.status(SUCCESS_GET_RESTAURANT_LIST.getHttpStatus())
			.body(Response.create(SUCCESS_GET_RESTAURANT_LIST, dummyList));
	}

	@GetMapping("/{restaurantId}")
	public ResponseEntity<?> getRestaurantSimple(@PathVariable Long restaurantId) {
		// 실제 로직: e.g. restaurantService.getRestaurantSimple(restaurantId)
		// 여기서는 임시 mock 데이터 예시
		RestaurantSimpleResponseDto dto = new RestaurantSimpleResponseDto();
		dto.setRestaurantName("김밥천국");
		dto.setRestaurantAddress("서울 종로구 어딘가");
		dto.setRestaurantLatitude(37.1234);
		dto.setRestaurantLongitude(127.5678);

		return ResponseEntity
			.status(SUCCESS_GET_SIMPLE_RESTAURANT.getHttpStatus()) // 200
			.body(Response.create(SUCCESS_GET_SIMPLE_RESTAURANT, dto));
	}

	@PostMapping("/custom")
	public ResponseEntity<?> createCustomMenu(@RequestBody CustomMenuRequestDto requestDto) {
		// 실제 등록 로직: e.g. customMenuService.createMenu(requestDto);
		return ResponseEntity
			.status(SUCCESS_CREATE_CUSTOM_MENU.getHttpStatus())  // 200
			.body(Response.create(SUCCESS_CREATE_CUSTOM_MENU, null));
	}

	@DeleteMapping("/custom/{customId}")
	public ResponseEntity<?> deleteCustomMenu(@PathVariable Long customId) {
		// 실제 비즈니스 로직: e.g. customMenuService.deleteCustomMenu(customId);

		return ResponseEntity
			.status(SUCCESS_DELETE_CUSTOM_MENU.getHttpStatus())  // 200
			.body(Response.create(SUCCESS_DELETE_CUSTOM_MENU, null));
	}

}
