package com.example.ddo_pay.restaurant.service.impl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import com.example.ddo_pay.common.config.S3.S3Service;
import com.example.ddo_pay.restaurant.dto.response.*;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.ddo_pay.common.exception.CustomException;
import com.example.ddo_pay.common.response.ResponseCode;
import com.example.ddo_pay.common.util.SecurityUtil;
import com.example.ddo_pay.restaurant.dto.request.CustomMenuRequestDto;
import com.example.ddo_pay.restaurant.dto.request.RestaurantCrawlingRequestDto;
import com.example.ddo_pay.restaurant.dto.request.RestaurantCreateRequestDto;
import com.example.ddo_pay.restaurant.dto.request.RestaurantDeleteRequestDto;
import com.example.ddo_pay.restaurant.entity.CustomMenu;
import com.example.ddo_pay.restaurant.entity.Menu;
import com.example.ddo_pay.restaurant.entity.Restaurant;
import com.example.ddo_pay.restaurant.entity.UserRestaurant;
import com.example.ddo_pay.restaurant.repository.CustomMenuRepository;
import com.example.ddo_pay.restaurant.repository.MenuRepository;
import com.example.ddo_pay.restaurant.repository.RestaurantRepository;
import com.example.ddo_pay.restaurant.repository.UserRestaurantRepository;
import com.example.ddo_pay.restaurant.service.RestaurantService;
import com.example.ddo_pay.user.entity.User;
import com.example.ddo_pay.user.repo.UserRepo;
import org.springframework.web.multipart.MultipartFile;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class RestaurantServiceImpl implements RestaurantService {

	private final RestaurantRepository restaurantRepository;
	private final UserRestaurantRepository userRestaurantRepository;
	private final MenuRepository menuRepository;
	private final CustomMenuRepository customMenuRepository;
	private final UserRepo userRepo; // 예: 사용자 식별을 위한 repository
	private final S3Service s3Service; // S3Service 주입

	/**
	 * 맛집 등록 로직
	 */
	@Override
	@Transactional
	public void createRestaurant(RestaurantCreateRequestDto requestDto) {

		if (requestDto.getUserId() == null) {
			throw new CustomException(
				ResponseCode.NO_EXIST_USER,
				"userId",
				"user_id가 누락되었습니다."
			);
		}

		// 1) 사용자 조회
		User user = userRepo.findById((long) requestDto.getUserId())
			.orElseThrow(() -> new CustomException(
				ResponseCode.NO_EXIST_USER,
				"userId",
				"해당 user가 존재하지 않습니다."
			));

		// 2) (placeName, addressName)로 Restaurant 조회
		Optional<Restaurant> existingRestaurantOpt = restaurantRepository.findByPlaceNameAndAddressName(
			requestDto.getPlaceName(),
			requestDto.getAddressName()
		);

		Restaurant restaurant;
		if (existingRestaurantOpt.isPresent()) {
			// 이미 같은 식당 존재 → 재사용
			restaurant = existingRestaurantOpt.get();
		} else {
			// 새로운 식당 엔티티 생성
			restaurant = Restaurant.builder()
				.placeName(requestDto.getPlaceName())
				.addressName(requestDto.getAddressName())
				.lat(requestDto.getPosition().getLat())
				.lng(requestDto.getPosition().getLng())
				.mainImageUrl(requestDto.getMainImageUrl())
				.userIntro(requestDto.getUserIntro())
				.starRating(requestDto.getStarRating())
				.build();

			restaurantRepository.save(restaurant);
		}

		// 3) UserRestaurant 중복 체크: 같은 user + 같은 restaurant id?
		Optional<UserRestaurant> existingUserRes = userRestaurantRepository
			.findByUser_IdAndRestaurant_Id(user.getId(), restaurant.getId());

		if (existingUserRes.isPresent()) {
			// 이미 이 유저가 해당 식당을 등록한 상태
			throw new CustomException(
				ResponseCode.DATA_ALREADY_EXISTS,
				"userId,restaurantId",
				"이미 등록된 맛집입니다."
			);
		}

		// 4) UserRestaurant 새로 생성
		UserRestaurant userRestaurant = UserRestaurant.builder()
			.user(user)
			.restaurant(restaurant)
			.visitedCount(requestDto.getVisitedCount()) // 기본값 0
			.build();

		userRestaurantRepository.save(userRestaurant);

		// 5) Menu 목록 등록
		//   만약 “Restaurant가 처음 생겼을 때만 Menu를 추가”한다면,
		//   “if (!existingRestaurantOpt.isPresent()) { ... }” 조건으로 분기할 수도 있음.
		if (requestDto.getMenu() != null && !requestDto.getMenu().isEmpty()) {
			requestDto.getMenu().forEach(menuDto -> {
				Menu menu = Menu.builder()
					.menuName(menuDto.getMenuName())
					.menuPrice(menuDto.getMenuPrice())
					.menuImage(menuDto.getMenuImage())
					.restaurant(restaurant) // 재사용 or 새로 만든 Restaurant
					.build();
				menuRepository.save(menu);
			});
		}

		// 6) CustomMenu 목록 등록
		//   마찬가지로 “if userRestaurant가 새로 생겼을 때만” 등 정책에 따라 분기 가능
		if (requestDto.getCustomMenu() != null && !requestDto.getCustomMenu().isEmpty()) {
			requestDto.getCustomMenu().forEach(customDto -> {
				CustomMenu customMenu = CustomMenu.builder()
					.customMenuName(customDto.getCustomMenuName())
					.customMenuPrice(customDto.getCustomMenuPrice())
					.customMenuImage(customDto.getCustomMenuImage())
					.userRestaurant(userRestaurant)
					.build();
				customMenuRepository.save(customMenu);
			});
		}

		log.info("맛집 등록 완료. restaurantId={}, userId={}",
			restaurant.getId(), user.getId());
	}


	/**
	 * 파일을 함께 받는 식당 등록 로직 (컨트롤러에서 호출)
	 */
	@Override
	@Transactional
	public void createRestaurant(RestaurantCreateRequestDto requestDto, MultipartFile mainImageFile, List<MultipartFile> customMenuImages) {
		// 1. 메인 이미지 파일이 있으면 S3에 업로드 후 URL 설정
		if (mainImageFile != null && !mainImageFile.isEmpty()) {
			try {
				String mainImageUrl = s3Service.uploadFile(mainImageFile);
				requestDto.setMainImageUrl(mainImageUrl);
			} catch (IOException e) {
				throw new RuntimeException("메인 이미지 업로드 실패", e);
			}
		}

		// 2. custom_menu에 포함된 이미지 파일 처리
		List<CustomMenuRequestDto> customMenus = requestDto.getCustomMenu();
		if (customMenus != null && !customMenus.isEmpty() && customMenuImages != null) {
			// JSON 배열 순서와 파일 순서가 일치한다고 가정
			for (int i = 0; i < customMenus.size(); i++) {
				if (i < customMenuImages.size()) {
					MultipartFile file = customMenuImages.get(i);
					if (file != null && !file.isEmpty()) {
						try {
							String customImageUrl = s3Service.uploadFile(file);
							customMenus.get(i).setCustomMenuImage(customImageUrl);
						} catch (IOException e) {
							throw new RuntimeException("커스텀 메뉴 이미지 업로드 실패", e);
						}
					}
				}
			}
		}

		// 3. 파일 처리 완료 후, 기존 식당 등록 로직 호출 (DB 저장)
		createRestaurant(requestDto);
	}



	/**
	 * 맛집 해제(삭제) 로직
	 */
	@Override
	@Transactional
	public void removeRestaurant(Long userId, Long restaurantId) {

		// 1) 사용자 조회
		User user = userRepo.findById(userId)
			.orElseThrow(() -> new CustomException(
				ResponseCode.NO_EXIST_USER,
				"userId",
				"해당 user가 존재하지 않습니다."
			));

		// 2) userId + restaurantId 로 UserRestaurant 조회
		UserRestaurant userRestaurant = userRestaurantRepository
			.findByUser_IdAndRestaurant_Id(user.getId(), restaurantId)
			.orElseThrow(() -> new CustomException(
				ResponseCode.NO_EXIST_RESTAURANT,
				"restaurantId",
				"등록된 맛집 정보가 없습니다."
			));



		// 3) 관계 해제 및 DB 삭제
		userRestaurantRepository.delete(userRestaurant);

		log.info("맛집 해제 완료. userId={}, restaurantId={}", user.getId(), restaurantId);
	}

	/**
	 * 등록된 맛집 리스트 조회
	 */
	@Override
	@Transactional(readOnly = true)
	public List<RestaurantListItemResponseDto> getRegisteredRestaurantList() {
		// 1) 현재 로그인 사용자 식별
		Long userId = SecurityUtil.getUserId();  // 가정

		// 2) userRestaurant 테이블에서 userId로 등록된 목록 조회
		List<UserRestaurant> userResList = userRestaurantRepository.findByUser_Id(userId);

		// 3) 각 UserRestaurant → Restaurant 정보 추출 + DTO 매핑
		List<RestaurantListItemResponseDto> result = new ArrayList<>();
		for (UserRestaurant ur : userResList) {
			Restaurant r = ur.getRestaurant();

			RestaurantListItemResponseDto dto = new RestaurantListItemResponseDto();
			dto.setId(r.getId());

			// resName → placeName
			dto.setPlaceName(r.getPlaceName());

			// resAddress → addressName
			dto.setAddressName(r.getAddressName());

			// resImage → mainImageUrl
			dto.setMainImageUrl(r.getMainImageUrl());

			// visitedCount
			dto.setVisitedCount(ur.getVisitedCount());

			// position
			ResponsePositionDto pos = new ResponsePositionDto();
			pos.setLat(r.getLat());  // resLat → lat
			pos.setLng(r.getLng());  // resLng → lng
			dto.setPosition(pos);

			result.add(dto);
			dto.setUserIntro(r.getUserIntro());    // DB나 다른 테이블에 있다면 적절히 가져와서 set
			dto.setStarRating(r.getStarRating());  // DB에 별점 컬럼이 있을 경우
			dto.setVisitedCount(ur.getVisitedCount());
		}

		return result;
	}

	/**
	 * 맛집 상세 조회
	 */
	@Override
	@Transactional(readOnly = true)
	public RestaurantDetailResponseDto getRestaurantDetail(Long restaurantId) {
		// 1) Restaurant 엔티티 조회
		Restaurant restaurant = restaurantRepository.findById(restaurantId)
			.orElseThrow(() -> new CustomException(
				ResponseCode.NO_EXIST_RESTAURANT,  // 예) 400, "등록된 식당이 아닙니다."
				"restaurantId",
				"해당 맛집이 존재하지 않습니다."
			));

		// 2) DTO 변환
		RestaurantDetailResponseDto detailDto = new RestaurantDetailResponseDto();
		detailDto.setRestaurantId(restaurant.getId());

		// resName → placeName
		detailDto.setPlaceName(restaurant.getPlaceName());

		// resAddress → addressName
		detailDto.setAddressName(restaurant.getAddressName());

		// resImage → mainImageUrl
		detailDto.setMainImageUrl(restaurant.getMainImageUrl());

		// 별점 (BigDecimal → double 변환)
		detailDto.setStarRating(
			restaurant.getStarRating() != null
				? restaurant.getStarRating().doubleValue()
				: 0.0
		);

		// userIntro
		detailDto.setUserIntro(restaurant.getUserIntro());

		// 위치 (위도, 경도)
		ResponsePositionDto pos = new ResponsePositionDto();
		pos.setLat(restaurant.getLat());
		pos.setLng(restaurant.getLng());
		detailDto.setPosition(pos);

		// 메뉴 목록
		List<MenuResponseDto> menuDtos = new ArrayList<>();
		for (Menu m : restaurant.getMenuList()) {
			MenuResponseDto mDto = new MenuResponseDto();
			mDto.setMenuId(m.getId());
			mDto.setMenuName(m.getMenuName());
			mDto.setMenuPrice(String.valueOf(m.getMenuPrice()));
			mDto.setMenuImage(m.getMenuImage());
			menuDtos.add(mDto);
		}
		detailDto.setMenu(menuDtos);

		// 커스텀 메뉴는 userRestaurant 통해 가져올 수도 있음(유저별로 다를 수 있으므로)
		// 여기서는 생략

		return detailDto;
	}

	/**
	 * 커스텀 메뉴 등록
	 */
	@Override
	@Transactional
	public void createCustomMenu(Long userId, CustomMenuRequestDto requestDto) {
		// 인증 토큰에서 전달된 userId를 사용하여 UserRestaurant 조회
		UserRestaurant userRestaurant = userRestaurantRepository
			.findByUser_IdAndRestaurant_Id(userId, requestDto.getRestaurantId())
			.orElseThrow(() -> new CustomException(
				ResponseCode.NO_EXIST_RESTAURANT,
				"restaurantId",
				"등록되지 않은 맛집입니다."
			));

		// 2) 중복 체크: 같은 사용자-맛집 관계에서 동일한 메뉴명이 존재하는지 확인
		boolean exists = customMenuRepository.existsByUserRestaurantAndCustomMenuName(
			userRestaurant, requestDto.getCustomMenuName());

		if (exists) {
			throw new CustomException(
				ResponseCode.DATA_ALREADY_EXISTS,
				"customMenuName",
				"이미 등록된 커스텀 메뉴입니다."
			);
		}


		CustomMenu customMenu = CustomMenu.builder()
			.customMenuName(requestDto.getCustomMenuName())
			.customMenuPrice(requestDto.getCustomMenuPrice())
			.customMenuImage(requestDto.getCustomMenuImage())
			.userRestaurant(userRestaurant)
			.build();

		customMenuRepository.save(customMenu);
		log.info("커스텀 메뉴 등록 완료. userId={}, restaurantId={}", userId, requestDto.getRestaurantId());
	}

	@Override
	@Transactional
	public void deleteCustomMenu(Long customId, Long userId) {
		// 존재 여부 확인
		CustomMenu customMenu = customMenuRepository.findById(customId)
			.orElseThrow(() -> new CustomException(
				ResponseCode.NO_EXIST_CUSTOM_MENU,
				"customId",
				"해당 커스텀 메뉴가 존재하지 않습니다."
			));

		// 소유자 확인: customMenu가 userId와 일치하는지 검증 (예, customMenu.getUserRestaurant().getUser().getId())
		if (!customMenu.getUserRestaurant().getUser().getId().equals(userId)) {
			throw new CustomException(
				ResponseCode.UNAUTHORIZED,
				"userId",
				"해당 메뉴를 삭제할 권한이 없습니다."
			);
		}

		customMenuRepository.delete(customMenu);
		log.info("커스텀 메뉴 삭제 완료. customId={}, userId={}", customId, userId);
	}

	@Override
	@Transactional
	public void saveCrawlingStoreData(RestaurantCrawlingStoreDto storeDto, Long userId) {
		// 1) 사용자 조회
		User user = userRepo.findById(userId)
			.orElseThrow(() -> new RuntimeException("해당 유저가 존재하지 않습니다."));

		// 2) storeDto에서 placeId 추출
		String placeId = storeDto.getPlaceId();
		if (placeId == null || placeId.isBlank()) {
			placeId = "unknown";
		}

		// 3) DB에서 placeId로 식당 조회
		Optional<Restaurant> existingOpt = restaurantRepository.findByPlaceId(placeId);

		final Restaurant restaurant;
		if (existingOpt.isPresent()) {
			// 이미 동일 placeId로 저장된 식당 재사용
			restaurant = existingOpt.get();
			// 필요하면 기존 식당 정보 업데이트 (lat, lng, starRating 등)
		} else {
			// 없는 placeId → 새 식당 생성
			restaurant = Restaurant.builder()
				.placeId(placeId)
				.placeName(storeDto.getPlaceName())
				.addressName(storeDto.getAddressName())
				.mainImageUrl(storeDto.getMainImageUrl())
				.lat(storeDto.getPosition() != null ? storeDto.getPosition().getLat() : null)
				.lng(storeDto.getPosition() != null ? storeDto.getPosition().getLng() : null)
				.userIntro(storeDto.getUserIntro())
				.starRating(storeDto.getStarRating())
				.build();

			restaurantRepository.save(restaurant);

			// 메뉴 목록 저장 (신규 식당에 한해 추가, 이미 있는 식당은 정책에 따라 추가 or 무시)
			if (storeDto.getMenus() != null) {
				for (RestaurantCrawlingMenuDto menuDto : storeDto.getMenus()) {
					Menu menu = Menu.builder()
						.menuName(menuDto.getMenuName())
						// menuPrice, menuImage 등 필요에 맞게
						.restaurant(restaurant)
						.build();
					menuRepository.save(menu);
				}
			}
		}

		// 4) (user, restaurant) 관계 중복 체크
		boolean alreadyExists = userRestaurantRepository
			.findByUser_IdAndRestaurant_Id(user.getId(), restaurant.getId())
			.isPresent();

		if (alreadyExists) {
			// 이미 등록된 맛집이면 예외
			throw new CustomException(ResponseCode.ALREADY_EXIST_RESTAURANT);
		}

		// 5) userRestaurant 새로 연결
		UserRestaurant userRestaurant = UserRestaurant.builder()
			.user(user)
			.restaurant(restaurant)
			.build();
		userRestaurantRepository.save(userRestaurant);

		log.info("크롤링 데이터로 DB 저장 완료: placeId={}, restaurantId={}, userId={}",
			placeId, restaurant.getId(), userId);
	}

	@Override
	@Transactional(readOnly = true)
	public List<RestaurantListItemResponseDto> getRegisteredRestaurantListByPosition(Double lat, Double lng) {
		// 위치 기반 로직 or 단순 전체 반환
		Long userId = SecurityUtil.getUserId();
		List<UserRestaurant> userResList = userRestaurantRepository.findByUser_Id(userId);

		// TODO: 필요하다면 lat, lng를 이용해 거리 필터/정렬 로직 구현

		List<RestaurantListItemResponseDto> result = new ArrayList<>();
		for (UserRestaurant ur : userResList) {
			Restaurant r = ur.getRestaurant();

			RestaurantListItemResponseDto dto = new RestaurantListItemResponseDto();
			dto.setId(r.getId());
			dto.setPlaceName(r.getPlaceName());
			dto.setAddressName(r.getAddressName());
			dto.setMainImageUrl(r.getMainImageUrl());
			dto.setVisitedCount(ur.getVisitedCount());

			ResponsePositionDto pos = new ResponsePositionDto();
			pos.setLat(r.getLat());
			pos.setLng(r.getLng());
			dto.setPosition(pos);

			// 필요시 userIntro, starRating 등 추가 매핑
			result.add(dto);
		}

		return result;
	}

}

