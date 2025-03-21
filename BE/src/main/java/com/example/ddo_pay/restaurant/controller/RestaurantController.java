package com.example.ddo_pay.restaurant.controller;

import static com.example.ddo_pay.common.response.ResponseCode.*;

import java.util.ArrayList;
import java.util.List;

import com.example.ddo_pay.common.response.Response;
import com.example.ddo_pay.common.response.ResponseCode;
import com.example.ddo_pay.restaurant.dto.request.CustomMenuRequestDto;
import com.example.ddo_pay.restaurant.dto.request.RestaurantCreateRequestDto;
import com.example.ddo_pay.restaurant.dto.request.RestaurantDeleteRequestDto;
import com.example.ddo_pay.restaurant.dto.response.ResponsePositionDto;
import com.example.ddo_pay.restaurant.dto.response.RestaurantListItemResponseDto;
import com.example.ddo_pay.restaurant.dto.response.RestaurantSimpleResponseDto;

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
			.status(ResponseCode.SUCCESS_CREATE_RESTAURANT.getHttpStatus()) // -> 204
			.body(Response.create(ResponseCode.SUCCESS_CREATE_RESTAURANT, null));
	}

	@DeleteMapping
	public ResponseEntity<?> removeRestaurant(@RequestBody RestaurantDeleteRequestDto requestDto) {
		Long restaurantId = requestDto.getRestaurantId();
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
		r1.setId(1L);
		r1.setPlaceName("김밥천국");
		r1.setRestaurantImage("http://example.com/images/kimbab.jpg");
		r1.setAddressName("서울 종로구 어딘가");
		r1.setVisitedCount(0);

		// position 생성 후 lat,lng 설정
		ResponsePositionDto pos1 = new ResponsePositionDto();
		pos1.setLat(37.1234);
		pos1.setLng(127.5678);
		r1.setPosition(pos1);



		RestaurantListItemResponseDto r2 = new RestaurantListItemResponseDto();
		r2.setId(2L);
		r2.setPlaceName("식당B");
		r2.setRestaurantImage("http://example.com/images/restaurant_b.jpg");
		r2.setAddressName("서울 강남구 어딘가");
		r2.setVisitedCount(3);

		ResponsePositionDto pos2 = new ResponsePositionDto();
		pos2.setLat(37.1234);
		pos2.setLng(127.5678);
		r2.setPosition(pos2);

		dummyList.add(r1);
		dummyList.add(r2);

		// 공통 Response로 감싸서 반환
		return ResponseEntity
			.status(SUCCESS_GET_RESTAURANT_LIST.getHttpStatus())
			.body(Response.create(SUCCESS_GET_RESTAURANT_LIST, dummyList));
	}

	@GetMapping("/{restaurantId}")
	public ResponseEntity<?> getSimpleRestaurantInfo(@PathVariable int restaurantId) {

		// 실제 DB 엔티티나, 크롤링, 임시 mock 데이터를 만든다고 가정
		// 예: RestaurantEntity entity = restaurantService.findById(restaurantId);

		// DTO 생성
		RestaurantSimpleResponseDto detail = new RestaurantSimpleResponseDto();
		detail.setId((long) restaurantId);  // DB PK라면 entity.getId() 등
		detail.setPlaceName("김밥천국");
		detail.setAddressName("서울 종로구 어딘가");

		ResponsePositionDto pos = new ResponsePositionDto();
		pos.setLat(37.1234);
		pos.setLng(127.5678);
		detail.setPosition(pos);

		return ResponseEntity
			.ok(
				Response.create(ResponseCode.SUCCESS_GET_SIMPLE_RESTAURANT, detail)
			);
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
