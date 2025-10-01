package com.kakao.together.external.s3;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("dev")
class AwsS3ServiceTest {

    @Autowired
    AwsS3Service awsS3Service;

    @Test
    void moveToStorageUpload() {
        awsS3Service.moveToStorageUpload("kakaotogether-file-bucket/imgs/temporary/c34a631c-baac-4f96-a6ec-9bdab29ad60d.png", "image/png");
    }
}