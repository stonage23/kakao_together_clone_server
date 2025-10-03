package com.kakao.together.file.storage.s3;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.CopyObjectRequest;
import com.amazonaws.services.s3.model.DeleteObjectRequest;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.kakao.together.file.controller.dto.RawMultipartFile;
import com.kakao.together.exception.CustomException;
import com.kakao.together.exception.ErrorCode;
import com.kakao.together.properties.AwsS3Properties;
import com.kakao.together.file.storage.FileStorageService;
import com.kakao.together.file.helper.FileValidator;
import com.kakao.together.file.helper.LogicalPathMapper;
import com.kakao.together.util.FileManageUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
@Profile("s3")
public class AwsS3Service implements FileStorageService {

    private final AwsS3Properties awsS3Properties;
    private final AmazonS3 s3Client;
    private final FileValidator fileValidator;
    private final LogicalPathMapper logicalPathMapper;


    public RawMultipartFile processTempFileUpload(MultipartFile file) {
        String originalFilename = file.getOriginalFilename();
        if (!fileValidator.isAllowedExtension(originalFilename)) {
            log.info("허용하지 않는 타입의 파일. filename: {}", file.getOriginalFilename());
            throw new CustomException(ErrorCode.UNSUPPORTED_FILE_FORMAT);
        }
        String extension = FileManageUtil.extractExtension(originalFilename);
        String createdFileName = createRealFilename() + extension;

        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentType(file.getContentType());
        metadata.setContentLength(file.getSize());

        String path = logicalPathMapper.getTempPrefix(file.getContentType()) + "/" + createdFileName;

        try {
            s3Client.putObject(awsS3Properties.getS3().getBucket(), path, file.getInputStream(), metadata);
        } catch (IOException e) {
            log.warn("AWS S3 스토리지에 파일 업로드 실패.", e);
            throw new CustomException(ErrorCode.FAILED_UPLOAD_FILE);
        }

        return RawMultipartFile.builder()
                .extension(extension)
                .contentType(file.getContentType())
                .size(file.getSize())
                .savedFileName(createdFileName)
                .originalFilename(file.getOriginalFilename())
                .build();
    }

    private String createRealFilename() {
        return UUID.randomUUID().toString();
    }

    @Override
    public void moveToStorageUpload(String fileName, String contentType) {

        String from = logicalPathMapper.getTempPrefix(contentType) + "/" + fileName;
        String to  = logicalPathMapper.getUploadPrefix(contentType) + "/" + fileName;

        CopyObjectRequest copyObjRequest = new CopyObjectRequest(
                awsS3Properties.getS3().getBucket(), from, awsS3Properties.getS3().getBucket(), to
        );

        s3Client.copyObject(copyObjRequest);

        DeleteObjectRequest deleteObjRequest = new DeleteObjectRequest(awsS3Properties.getS3().getBucket(), from);
        s3Client.deleteObject(deleteObjRequest);
    }

    @Override
    public void deleteFile(String filename, String contentType) {

        String pathPrefix = logicalPathMapper.getUploadPrefix(contentType);
        String key = pathPrefix + "/" + filename;

        DeleteObjectRequest deleteObjRequest = new DeleteObjectRequest(awsS3Properties.getS3().getBucket(), key);
        s3Client.deleteObject(deleteObjRequest);
    }
}
