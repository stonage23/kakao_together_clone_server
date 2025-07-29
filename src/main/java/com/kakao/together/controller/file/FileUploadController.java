package com.kakao.together.controller.file;

import com.kakao.together.controller.dto.FileDto;
import com.kakao.together.controller.dto.ImageDto;
import com.kakao.together.service.file.FileService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class FileUploadController {

    private final FileService<ImageDto> imageService;
    private final FileService<FileDto> fileService;

    @PostMapping("/images/temp")
    public ResponseEntity<Object> tempImage(@RequestParam MultipartFile file) {
        return ResponseEntity.ok(imageService.saveFile(file));
    }

    @PostMapping("/images/upload")
    public ResponseEntity<Void> uploadImage(@RequestBody List<ImageDto> images) {
        imageService.moveFile(images);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/files/temp")
    public ResponseEntity<Object> tempFile(@RequestParam MultipartFile file) {
        return ResponseEntity.ok(fileService.saveFile(file));
    }
}
