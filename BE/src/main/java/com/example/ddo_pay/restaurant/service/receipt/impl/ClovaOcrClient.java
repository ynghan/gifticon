package com.example.ddo_pay.restaurant.service.receipt.impl;

import java.util.Base64;
import java.util.List;

import com.example.ddo_pay.restaurant.dto.receipt.OcrResponseDto;
import com.example.ddo_pay.restaurant.dto.receipt.OcrRequestDto;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;



@Service
@RequiredArgsConstructor
public class ClovaOcrClient {

	private final WebClient.Builder webClientBuilder;

	@Value("${clova.ocr.url}") // 예: https://cbgrx5natw.apigw.ntruss.com/custom/v1/{DomainId}/{InvokeKey}/document/receipt
	private String ocrUrl;

	@Value("${clova.ocr.secret}")
	private String ocrSecret;

	/**
	 * Multipart/form-data 방식으로 Clova OCR API 호출
	 * @param file 업로드할 파일 (영수증 이미지)
	 * @return OCR 응답 DTO
	 */
	public OcrResponseDto requestOcr(MultipartFile file) {
		try {
			// 1) message 파트 구성
			// API 문서 예시와 같이 JSON 문자열을 구성합니다.
			// timestamp는 현재 시간(ms)로 설정할 수 있습니다.
			String messageJson = String.format(
				"{\"version\": \"V2\", \"requestId\": \"1234\", \"timestamp\": %d, \"images\": [{\"format\": \"jpg\", \"name\": \"receipt_test\"}]}",
				System.currentTimeMillis()
			);

			// 2) file 파트 구성
			// MultipartFile을 ByteArrayResource로 감싸서 전송
			ByteArrayResource fileResource = new ByteArrayResource(file.getBytes()){
				@Override
				public String getFilename() {
					return file.getOriginalFilename();
				}
			};

			// 3) MultiValueMap에 각 파트를 추가
			MultiValueMap<String, Object> formData = new LinkedMultiValueMap<>();
			formData.add("message", messageJson);
			formData.add("file", fileResource);

			// 4) WebClient로 요청 전송
			OcrResponseDto response = webClientBuilder.build()
				.post()
				.uri(ocrUrl)
				.header("X-OCR-SECRET", ocrSecret)
				.contentType(MediaType.MULTIPART_FORM_DATA)
				.body(BodyInserters.fromMultipartData(formData))
				.retrieve()
				.bodyToMono(OcrResponseDto.class)
				.block();

			return response;
		} catch (Exception e) {
			throw new RuntimeException("OCR 요청 실패: " + e.getMessage(), e);
		}
	}
}