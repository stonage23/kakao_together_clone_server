package com.kakao.together.mock;

import com.kakao.together.domain.repository.FundraisingRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Component
@Profile("mock") // "dev" 프로필에서만 실행
@RequiredArgsConstructor
public class TestDataInitializer implements CommandLineRunner {

    private final MockDataService mockDataService;
    private final FundraisingRepository fundraisingRepository;

    @Override
    public void run(String... args) throws Exception {
        // DB에 데이터가 없을 때만 실행
//        if (fundraisingRepository.count() < 10) {
//            mockDataService.generateMockData();
//        }
    }
}