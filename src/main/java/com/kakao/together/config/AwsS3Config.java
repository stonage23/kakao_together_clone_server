package com.kakao.together.config;

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.kakao.together.properties.AwsS3Properties;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class AwsS3Config {

    private final AwsS3Properties awsS3Properties;

    @Bean
    public AmazonS3 amazonS3() {
        BasicAWSCredentials awsCreds = new BasicAWSCredentials(awsS3Properties.getCredentials().getAccessKey(), awsS3Properties.getCredentials().getSecretKey());
        return AmazonS3ClientBuilder.standard()
                .withRegion(awsS3Properties.getRegion().getStatics())
                .withCredentials(new AWSStaticCredentialsProvider(awsCreds))
                .build();
    }
}
