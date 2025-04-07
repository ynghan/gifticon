package com.example.ddo_pay.restaurant.service.crawling;

import com.example.ddo_pay.restaurant.dto.request.RestaurantCrawlingRequestDto;
import com.example.ddo_pay.restaurant.dto.response.ResponsePositionDto;
import com.example.ddo_pay.restaurant.dto.response.RestaurantCrawlingMenuDto;
import com.example.ddo_pay.restaurant.dto.response.RestaurantCrawlingStoreDto;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import okhttp3.HttpUrl;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import java.math.BigDecimal;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

@Service
@Slf4j
public class NaverCrawlingServiceImpl implements NaverCrawlingService {

	private final RedisTemplate<String, String> redisTemplate;
	private final OkHttpClient httpClient = new OkHttpClient();
	private final ObjectMapper objectMapper = new ObjectMapper();

	public NaverCrawlingServiceImpl(RedisTemplate<String, String> redisTemplate) {
		this.redisTemplate = redisTemplate;
	}

	/**
	 * allSearch와 GraphQL API를 호출하여 매장 기본 정보를 추출하고,
	 * 최종 JSON 구조에 맞는 DTO를 구성합니다.
	 *
	 * 최종 결과는 아래와 같이 구성됩니다:
	 *
	 * - place_name: HTML 태그 제거된 매장명 (res_name)
	 * - main_image_url: 대표 이미지 URL (res_image)
	 * - address_name: 매장 주소 (res_address)
	 * - position: { lat, lng } (boundary 값을 이용)
	 * - place_id: 매장 고유 ID
	 * - menus: menuInfo 문자열을 파싱하여 생성된 메뉴 목록 (각 메뉴는 menu_name, menu_price 포함)
	 */
	@Override
	public List<RestaurantCrawlingStoreDto> getCrawlingInfo(RestaurantCrawlingRequestDto requestDto) {
		// 1) 검색어 구성: placeName과 addressName(앞 두 단어)를 결합
		String placeNameQuery = requestDto.getPlaceName();
		String addressName = requestDto.getAddressName();
		if (addressName != null && !addressName.isEmpty()) {
			String[] tokens = addressName.trim().split("\\s+");
			if (tokens.length >= 2) {
				// 예: "서울 송파구"
				String shortAddress = tokens[0] + " " + tokens[1];
				placeNameQuery = placeNameQuery + " " + shortAddress;
			}
		}

		// 2) 구성한 검색어로 전체 검색 수행
		List<Map<String, Object>> searchResults = callAllSearchList(placeNameQuery);
		log.info("callAllSearchList 결과: {}", searchResults);

		// 검색 결과가 없으면 빈 리스트 반환
		if (searchResults == null || searchResults.isEmpty()) {
			return Collections.emptyList();
		}

		// 3) 검색 결과 중 첫 번째 결과를 선택
		Map<String, Object> chosenResult = searchResults.get(0);

		// 4) 선택된 결과를 기반으로 DTO 구성
		RestaurantCrawlingStoreDto dto = new RestaurantCrawlingStoreDto();
		String rawName = (String) chosenResult.get("res_name");
		String placeName = rawName != null ? rawName.replaceAll("<.*?>", "") : "";
		dto.setPlaceName(placeName);
		dto.setMainImageUrl((String) chosenResult.get("res_image"));
		dto.setAddressName((String) chosenResult.get("res_address"));
		dto.setPlaceId((String) chosenResult.get("place_id"));

		// 별점 문자열을 BigDecimal로 변환 (예외 발생 시 0으로 처리)
		String starRatingStr = (String) chosenResult.get("star_rating");
		try {
			dto.setStarRating(starRatingStr != null ? new BigDecimal(starRatingStr) : BigDecimal.ZERO);
		} catch (Exception e) {
			dto.setStarRating(BigDecimal.ZERO);
		}

		// 위치 정보 설정
		Double resLat = (Double) chosenResult.get("res_lat");
		Double resLng = (Double) chosenResult.get("res_lng");
		if (resLat != null && resLng != null) {
			ResponsePositionDto pos = new ResponsePositionDto();
			pos.setLat(resLat);
			pos.setLng(resLng);
			dto.setPosition(pos);
		}

		// 메뉴 정보 파싱
		String menuStr = (String) chosenResult.get("menu_str");
		dto.setMenus(parseMenus(menuStr));

		// 5) GraphQL API 호출 (추가 정보 조회 - 로그 출력)
		String placeId = (String) chosenResult.get("place_id");
		if (placeId != null) {
			JsonNode gqlNode = callGraphqlForDetail(placeId);
			log.info("GraphQL PlaceID: {}", gqlNode.toPrettyString());
			BigDecimal gqlAvgRating = extractAvgRatingFromGraphql(gqlNode);
			log.info("GraphQL avgRating: {}", gqlAvgRating.toString());
			dto.setStarRating(gqlAvgRating);
		}

		List<RestaurantCrawlingStoreDto> results = new ArrayList<>();
		results.add(dto);
		return results;
	}


	@Override
	public List<RestaurantCrawlingStoreDto> getOrLoadCrawlingResult(RestaurantCrawlingRequestDto req) {
		// 1. 먼저 크롤링 정보를 가져옵니다.
		List<RestaurantCrawlingStoreDto> result = this.getCrawlingInfo(req);

		if (!result.isEmpty()) {
			// 2. 첫 번째 결과에서 placeId를 추출합니다.
			String placeId = result.get(0).getPlaceId();
			if (placeId != null && !placeId.isBlank()) {
				String redisKey = "crawling:" + placeId;
				// 3. 캐시 조회: Redis에 저장된 결과가 있으면 반환합니다.
				String cachedJson = redisTemplate.opsForValue().get(redisKey);
				if (cachedJson != null) {
					try {
						return objectMapper.readValue(cachedJson, new TypeReference<List<RestaurantCrawlingStoreDto>>() {});
					} catch (Exception e) {
						log.warn("캐시 역직렬화 실패 → 새 크롤링 진행", e);
					}
				}
				// 4. 캐시에 결과 저장
				try {
					String toCache = objectMapper.writeValueAsString(result);
					redisTemplate.opsForValue().set(redisKey, toCache, Duration.ofHours(4));
				} catch (Exception e) {
					log.warn("캐시 직렬화 실패", e);
				}
			}
		}
		return result;
	}


	// ====== AllSearch API 호출 및 파싱 ======
	private List<Map<String, Object>> callAllSearchList(String query) {
		List<Map<String, Object>> results = new ArrayList<>();

		try {
			// 1) URL 생성
			HttpUrl url = HttpUrl.parse("https://map.naver.com/p/api/search/allSearch")
				.newBuilder()
				.addQueryParameter("query", query)
				.addQueryParameter("type", "all")
				.addQueryParameter("searchCoord", "129.0680439000028;35.154616899999")
				.addQueryParameter("boundary", "")
				.build();

			// 2) Request 헤더 구성
			String encodedQuery = URLEncoder.encode(query, StandardCharsets.UTF_8);
			Request request = new Request.Builder()
				.url(url)
				.header("Accept", "application/json, text/plain, */*")
				.header("Accept-Language", "ko-KR,ko;q=0.9,en-US;q=0.8,en;q=0.7")
				.header("Origin", "https://map.naver.com")
				.header("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/134.0.0.0 Safari/537.36")
				.header("Cookie", "NNB=R3LR2TRSDV5WO; NID_AUT=your_actual_cookie_value;")
				.header("Referer", "https://map.naver.com/p/search/" + encodedQuery + "?c=15.00,0,0,0,dh&isCorrectAnswer=true")
				.build();

			// 3) 요청 & 응답 파싱
			try (Response response = httpClient.newCall(request).execute()) {
				if (!response.isSuccessful()) {
					log.warn("allSearch 호출 실패: {}", response);
					return results; // 빈 리스트 반환
				}

				String bodyStr = response.body().string();
				JsonNode root = objectMapper.readTree(bodyStr);

				// 4) boundary(위도/경도) 추출
				double lat = 0.0;
				double lng = 0.0;
				JsonNode boundary = root.path("result").path("place").path("boundary");
				if (boundary.isArray() && boundary.size() >= 2) {
					lng = boundary.get(0).asDouble();
					lat = boundary.get(1).asDouble();
				}

				// 5) 매장 리스트 추출
				JsonNode listArr = root.path("result").path("place").path("list");
				if (listArr.isArray()) {
					for (JsonNode item : listArr) {
						Map<String, Object> map = new HashMap<>();
						map.put("place_id", item.path("id").asText(null));
						map.put("res_name", item.path("display").asText(null));
						map.put("res_address", item.path("address").asText(null));

						// 대표 이미지
						JsonNode thumUrls = item.path("thumUrls");
						if (thumUrls.isArray() && thumUrls.size() > 0) {
							map.put("res_image", thumUrls.get(0).asText(null));
						} else {
							map.put("res_image", null);
						}

						map.put("star_rating", item.path("avgRating").asText(null));
						map.put("menu_str", item.path("menuInfo").asText(null));

						// boundary에서 추출한 위도, 경도
						map.put("res_lat", lat);
						map.put("res_lng", lng);

						results.add(map);
					}
				}
			}
		} catch (Exception e) {
			log.error("callAllSearchList error: ", e);
		}

		return results;
	}


	private Map<String, Object> parsePlaceInfo(JsonNode data) {
		Map<String, Object> result = new HashMap<>();
		// 기본값 설정
		result.put("place_id", null);
		result.put("res_name", null);
		result.put("res_address", null);
		result.put("res_lat", null);
		result.put("res_lng", null);
		result.put("res_image", null);
		result.put("star_rating", null);
		result.put("menu_str", null);

		// "result" → "place" 노드 추출
		JsonNode placeData = data.path("result").path("place");

		// boundary 배열: 경도와 위도
		JsonNode boundary = placeData.path("boundary");
		if (boundary.isArray() && boundary.size() >= 2) {
			result.put("res_lng", boundary.get(0).asDouble());
			result.put("res_lat", boundary.get(1).asDouble());
		}

		// 매장 리스트 추출
		JsonNode placeList = placeData.path("list");
		if (!placeList.isArray() || placeList.size() == 0) {
			return result;  // 검색 결과 없음
		}

		// 첫 번째 매장 정보 사용
		JsonNode firstPlace = placeList.get(0);
		result.put("place_id", firstPlace.path("id").asText(null));
		result.put("res_name", firstPlace.path("display").asText(null));
		result.put("res_address", firstPlace.path("address").asText(null));

		// 대표 이미지: thumUrls 배열의 첫 번째 값 사용
		JsonNode thumUrls = firstPlace.path("thumUrls");
		if (thumUrls.isArray() && thumUrls.size() > 0) {
			result.put("res_image", thumUrls.get(0).asText(null));
		}

		result.put("star_rating", firstPlace.path("avgRating").asText(null));
		result.put("menu_str", firstPlace.path("menuInfo").asText(null));

		return result;
	}




	// ====== GraphQL API 호출 ======
	private JsonNode callGraphqlForDetail(String placeId) {
		try {
			String gqlBody = """
                    [
                      {
                        "operationName":"getAnnouncements",
                        "variables":{
                          "businessId":"%s",
                          "businessType":"restaurant",
                          "deviceType":"pcmap"
                        },
                        "query": "query getAnnouncements($businessId: String!, $businessType: String!, $deviceType: String!) { announcements: announcementsViaCP0(businessId: $businessId, businessType: $businessType, deviceType: $deviceType ) { feedId title period thumbnail { url count isVideo } } }"
                      }
                    ]
                    """.formatted(placeId);
			RequestBody reqBody = RequestBody.create(gqlBody, MediaType.parse("application/json"));
			HttpUrl url = HttpUrl.parse("https://pcmap-api.place.naver.com/graphql")
				.newBuilder().build();
			Request request = new Request.Builder()
				.url(url)
				.post(reqBody)
				// GraphQL 요청에도 브라우저와 동일한 User-Agent 사용
				.header("User-Agent", getFakeUserAgent())
				// Referer도 브라우저에서 사용하는 형식으로 설정
				.header("Referer", "https://pcmap.place.naver.com/restaurant/" + placeId + "/home?from=map&locale=ko")
				.header("Content-Type", "application/json")
				.build();

			try (Response response = httpClient.newCall(request).execute()) {
				if (!response.isSuccessful()) {
					log.warn("callGraphqlForDetail 실패: {}", response);
					return objectMapper.createArrayNode();
				}
				String bodyStr = response.body().string();
				return objectMapper.readTree(bodyStr);
			}
		} catch (Exception e) {
			log.error("callGraphqlForDetail error", e);
			return objectMapper.createArrayNode();
		}
	}

	// 가짜 User-Agent를 반환하는 메서드 예시
	private String getFakeUserAgent() {
		String[] userAgents = new String[] {
			"Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/105.0.5195.102 Safari/537.36",
			"Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/104.0.5112.102 Safari/537.36",
			"Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/605.1.15 (KHTML, like Gecko) Version/15.1 Safari/605.1.15",
			"Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/103.0.5060.134 Safari/537.36"
		};
		int index = new Random().nextInt(userAgents.length);
		return userAgents[index];
	}

	private BigDecimal extractAvgRatingFromGraphql(JsonNode gqlResponse) {
		log.debug("GraphQL 응답 전체: {}", gqlResponse.toPrettyString());
		if (gqlResponse != null && gqlResponse.isArray()) {
			for (JsonNode node : gqlResponse) {
				log.debug("현재 노드: {}", node.toPrettyString());
				// visitorReviewStats 내 review.avgRating 확인
				if (node.has("visitorReviewStats")) {
					JsonNode visitorStats = node.get("visitorReviewStats");
					log.debug("visitorReviewStats 노드: {}", visitorStats.toPrettyString());
					JsonNode reviewNode = visitorStats.path("review");
					log.debug("review 노드: {}", reviewNode.toPrettyString());
					JsonNode avgRatingNode = reviewNode.path("avgRating");
					log.debug("avgRating 노드 (review 내부): {}", avgRatingNode);
					if (!avgRatingNode.isMissingNode() && avgRatingNode.isNumber()) {
						BigDecimal rating = BigDecimal.valueOf(avgRatingNode.asDouble());
						log.debug("추출된 avgRating (review 내부): {}", rating);
						return rating;
					}
				}
				// visitorReviewStats 경로가 없는 경우, 최상위 avgRating 확인
				JsonNode avgRatingNode = node.path("avgRating");
				log.debug("최상위 avgRating 노드: {}", avgRatingNode);
				if (!avgRatingNode.isMissingNode() && avgRatingNode.isNumber()) {
					BigDecimal rating = BigDecimal.valueOf(avgRatingNode.asDouble());
					log.debug("추출된 최상위 avgRating: {}", rating);
					return rating;
				}
			}
		}
		return BigDecimal.ZERO;
	}


	// ====== 메뉴 문자열 파싱 ======
	private List<RestaurantCrawlingMenuDto> parseMenus(String menuStr) {
		List<RestaurantCrawlingMenuDto> menus = new ArrayList<>();
		if (menuStr == null || menuStr.isEmpty()) {
			return menus;
		}
		String[] parts = menuStr.split("\\|");
		for (String part : parts) {
			part = part.trim();
			if (!part.isEmpty()) {
				int lastSpace = part.lastIndexOf(" ");
				RestaurantCrawlingMenuDto menu = new RestaurantCrawlingMenuDto();
				if (lastSpace != -1) {
					String name = part.substring(0, lastSpace).trim();
					String price = part.substring(lastSpace + 1).trim();
					menu.setMenuName(name);
					menu.setMenuPrice(price);
				} else {
					menu.setMenuName(part);
					menu.setMenuPrice("");
				}
				menus.add(menu);
			}
		}
		return menus;
	}
}
