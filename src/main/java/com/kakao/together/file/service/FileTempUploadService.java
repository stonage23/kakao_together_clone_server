package com.kakao.together.file.service;

import com.kakao.together.file.controller.dto.FileDto.FileResponse;
import org.springframework.web.multipart.MultipartFile;

public interface FileTempUploadService {
    FileResponse processTempUpload(MultipartFile file);
}
