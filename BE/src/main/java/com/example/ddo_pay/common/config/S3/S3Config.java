package com.example.ddo_pay.common.config.S3;

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "cloud.aws")
@Getter
@Setter
public class S3Config {
    private Credentials credentials;
    private Region region;

    @Getter @Setter
    public static class Credentials {
        private String accessKey;
        private String secretKey;
    }

    @Getter @Setter
    public static class Region {
        private String static_;
    }

    @Bean
    public AmazonS3Client amazonS3Client() {
        BasicAWSCredentials awsCredentials = new BasicAWSCredentials(
                credentials.accessKey,
                credentials.secretKey
        );
        return (AmazonS3Client) AmazonS3ClientBuilder.standard()
                .withRegion(region.static_)
                .withCredentials(new AWSStaticCredentialsProvider(awsCredentials))
                .build();
    }
}
