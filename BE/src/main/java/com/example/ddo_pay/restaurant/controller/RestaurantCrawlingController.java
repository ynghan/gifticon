package com.example.ddo_pay.restaurant.controller;

import java.util.List;

import com.example.ddo_pay.restaurant.service.NaverCrawlingService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/restaurants")
@RequiredArgsConstructor
public class RestaurantCrawlingController {

	private final NaverCrawlingService naverCrawlingService;

	/**
	 * GET /api/restaurants/crawling?lat=37.1234&lng=127.5678
	 *
	 * 사용자가 직접 위도,경도를 쿼리파라미터로 전달.
	 * 예) http://localhost:8080/api/restaurant/crawling?lat=37.1234&lng=127.5678
	 */
	@GetMapping("/crawling")
	public ResponseEntity<?> crawlByAddress(@RequestParam String address) {
		var details = naverCrawlingService.crawlStoreDetailsByAddress(address);
		return ResponseEntity.ok(details);
	}
}
