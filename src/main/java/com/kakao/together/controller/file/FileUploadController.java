package com.kakao.together.controller.file;

import com.kakao.together.controller.file.dto.FileDto.FileResponse;
import com.kakao.together.service.file.FileService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequiredArgsConstructor
public class FileUploadController {

    private final FileService fileService;

    @PostMapping("/files/temp")
    public ResponseEntity<FileResponse> uploadTempFile(@RequestParam MultipartFile file) {
        return ResponseEntity.ok(fileService.processTempUpload(file));
    }
}
