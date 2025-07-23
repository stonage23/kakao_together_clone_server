package com.kakao.together.controller.image;

import com.kakao.together.controller.dto.ImageDto;
import com.kakao.together.service.image.FileService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequiredArgsConstructor
public class FileUploadController {

    private final FileService<ImageDto> imageService;

    @PostMapping("/images/temp")
    public ResponseEntity<Object> tempImage(@RequestParam MultipartFile file) {
        return ResponseEntity.ok(imageService.saveTempImage(file));
    }
}
