package com.example.ddo_pay.restaurant.service.receipt.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import com.example.ddo_pay.restaurant.dto.receipt.AllSearchResponse;

@Service
@RequiredArgsConstructor
public class NaverMapSearchService {
	private final WebClient webClient = WebClient.builder()
		.baseUrl("https://map.naver.com")
		.defaultHeader(HttpHeaders.USER_AGENT, "Mozilla/5.0 ...")
		.defaultHeader(HttpHeaders.REFERER, "https://map.naver.com/")
		.build();

	public String getPlaceIdFromQuery(String query) {
		AllSearchResponse result = webClient.get()
			.uri(uriBuilder -> uriBuilder
				.path("/p/api/search/allSearch")
				.queryParam("query", query)
				.build()
			)
			.retrieve()
			.bodyToMono(AllSearchResponse.class)
			.block();

		if (result != null && result.getResult() != null
			&& result.getResult().getPlace() != null
			&& result.getResult().getPlace().getList() != null
			&& !result.getResult().getPlace().getList().isEmpty()) {

			return result.getResult().getPlace().getList().get(0).getId();
		}
		return null;
	}
}

