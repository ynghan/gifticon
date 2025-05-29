package com.example.ddo_pay.common.config.S3;

import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.ListObjectsV2Request;
import com.amazonaws.services.s3.model.ListObjectsV2Result;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.S3ObjectSummary;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class S3Service {

	private final AmazonS3Client amazonS3Client;

	@Value("${cloud.aws.s3.bucket}")
	private String bucket;

	// 외부 설정에서 prefix를 받아오도록 (기본값은 "uploads")
	@Value("${cloud.aws.s3.prefix:uploads}")
	private String prefix;

	public String uploadFile(MultipartFile file) throws IOException {
		String originalFileName = file.getOriginalFilename();
		String uniqueFileName = UUID.randomUUID().toString() + "_" + originalFileName;

		ObjectMetadata metadata = new ObjectMetadata();
		metadata.setContentType(file.getContentType());
		metadata.setContentLength(file.getSize());

		String s3Key = prefix + "/" + uniqueFileName;
		amazonS3Client.putObject(bucket, s3Key, file.getInputStream(), metadata);
		return amazonS3Client.getUrl(bucket, s3Key).toString();
	}

	// 추가: 폴더 내의 이미지 목록을 조회하고 랜덤 URL 반환
	public String getRandomImageUrl(String folder) {
		String basePath = "generated_images/";
		// 폴더 내 객체 목록 조회 (예: "congratulations/" 폴더)
		ListObjectsV2Request request = new ListObjectsV2Request()
			.withBucketName(bucket)
			.withPrefix(basePath + folder + "/");
		ListObjectsV2Result result = amazonS3Client.listObjectsV2(request);
		List<S3ObjectSummary> summaries = result.getObjectSummaries();

		// S3 내의 객체 목록에서 URL 목록 생성
		List<String> urls = new ArrayList<>();
		for (S3ObjectSummary summary : summaries) {
			String key = summary.getKey();
			String url = amazonS3Client.getUrl(bucket, key).toString();
			urls.add(url);
		}

		if (urls.isEmpty()) {
			throw new RuntimeException("No images found in folder: " + folder);
		}

		// 랜덤으로 하나의 URL 선택
		Random random = new Random();
		int index = random.nextInt(urls.size());
		return urls.get(index);
	}
}
