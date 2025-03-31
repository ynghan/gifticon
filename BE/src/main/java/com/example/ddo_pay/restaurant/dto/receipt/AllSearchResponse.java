package com.example.ddo_pay.restaurant.dto.receipt;

import lombok.Data;

import java.util.List;

@Data
public class AllSearchResponse {
	private Result result;

	@Data
	public static class Result {
		private Place place;
	}

	@Data
	public static class Place {
		private List<PlaceItem> list;
	}

	@Data
	public static class PlaceItem {
		private String id;
		private String name;
		private String address;
		// ...
	}
}
