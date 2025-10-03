package com.kakao.together.file.controller;

import com.kakao.together.file.controller.dto.FileDto.FileResponse;
import com.kakao.together.file.service.FileTempUploadService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequiredArgsConstructor
public class FileTempUploadController {

    private final FileTempUploadService fileTempUploadService;

    @PostMapping("/files/temp")
    public ResponseEntity<FileResponse> uploadTempFile(@RequestParam MultipartFile file) {
        return ResponseEntity.ok(fileTempUploadService.processTempUpload(file));
    }
}
