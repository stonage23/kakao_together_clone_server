package com.kakao.together;

import com.kakao.together.properties.AwsS3Properties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication(exclude = SecurityAutoConfiguration.class)
@EnableConfigurationProperties(value = AwsS3Properties.class)
public class KakaoTogetherCloneApplication {

	public static void main(String[] args) {
		SpringApplication.run(KakaoTogetherCloneApplication.class, args);
	}

}
