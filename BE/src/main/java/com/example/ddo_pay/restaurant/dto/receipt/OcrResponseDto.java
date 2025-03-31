package com.example.ddo_pay.restaurant.dto.receipt;

import java.util.List;

import lombok.Data;

@Data
public class OcrResponseDto {
	private List<OcrImage> images;

	@Data
	public static class OcrImage {
		// general 모델에서 필드명이 실제 응답에 따라 다를 수 있으므로
		// 일단 raw 전체를 받아서 필요한 부분만 추출 가능.
		private OcrStoreInfo storeInfo;
		private List<OcrAddress> addresses;
		// ...
	}

	@Data
	public static class OcrStoreInfo {
		private String name;
		private String text; // ...
	}

	@Data
	public static class OcrAddress {
		private String name;
		private String text;
		// ...
	}
}
