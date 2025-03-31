package com.example.ddo_pay.restaurant.service.receipt.impl;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import com.example.ddo_pay.restaurant.dto.receipt.OcrResponseDto;

@Service
@RequiredArgsConstructor
public class OcrProcessService {
	private final ClovaOcrClient clovaOcrClient;

	public ExtractedPlaceDto extractPlaceInfo(MultipartFile file) {
		// ClovaOcrClient가 multipart/form-data 형식의 요청을 구성하도록 변경했으므로,
		// 파일을 그대로 전달합니다.
		OcrResponseDto resDto = clovaOcrClient.requestOcr(file);
		if (resDto == null || resDto.getImages() == null || resDto.getImages().isEmpty()) {
			throw new RuntimeException("OCR 결과가 비어있습니다.");
		}

		// 결과 파싱
		OcrResponseDto.OcrImage firstImg = resDto.getImages().get(0);
		String placeName = "";
		String addressName = "";

		if (firstImg.getStoreInfo() != null) {
			placeName = firstImg.getStoreInfo().getText();
		}
		if (firstImg.getAddresses() != null && !firstImg.getAddresses().isEmpty()) {
			addressName = firstImg.getAddresses().get(0).getText();
		}

		return new ExtractedPlaceDto(placeName, addressName);
	}

	// OCR 결과를 단순화한 DTO
	@Data
	@AllArgsConstructor
	public static class ExtractedPlaceDto {
		private String placeName;
		private String addressName;
	}
}
