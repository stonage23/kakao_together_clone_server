package com.kakao.together.service.file;

import com.kakao.together.controller.file.dto.FileDto.FileResponse;
import org.springframework.web.multipart.MultipartFile;

public interface FileService {
    FileResponse processTempUpload(MultipartFile file);
}
