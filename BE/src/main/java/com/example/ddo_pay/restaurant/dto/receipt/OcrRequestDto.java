package com.example.ddo_pay.restaurant.dto.receipt;

import lombok.Data;

@Data
public class OcrRequestDto {
	private String version; // e.g. "V2"
	private RequestImage[] images;

	@Data
	public static class RequestImage {
		private String format; // "jpg/png"
		private String name;   // "demo"
		private String data;   // base64-encoded image
	}
}