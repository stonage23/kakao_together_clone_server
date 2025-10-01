package com.kakao.together.external.s3;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.CopyObjectRequest;
import com.amazonaws.services.s3.model.DeleteObjectRequest;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.kakao.together.controller.file.dto.RawMultipartFile;
import com.kakao.together.exception.CustomException;
import com.kakao.together.exception.ErrorCode;
import com.kakao.together.properties.AwsS3Properties;
import com.kakao.together.service.file.FileStorageService;
import com.kakao.together.service.file.impl.FileValidator;
import com.kakao.together.util.FileManageUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
@Profile("s3")
public class AwsS3Service implements FileStorageService {

    private final AwsS3Properties awsS3Properties;
    private final AmazonS3 s3Client;
    private final FileValidator fileValidator;

    @Value("${storage.image.temporary}")
    private String IMAGE_TEMPORARY;
    @Value("${storage.image.upload}")
    private String IMAGE_UPLOAD;


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

        String bucketPath = awsS3Properties.getS3().getBucket() + awsS3Properties.getImgs().getTemporary();

        try {
            s3Client.putObject(bucketPath, createdFileName, file.getInputStream(), metadata);
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
    public void moveToStorageUpload(String url, String contentType) {
        String fileName = Paths.get(url).getFileName().toString();

        String from = removeBucket(IMAGE_TEMPORARY) + "/" + fileName;
        String to = removeBucket(IMAGE_UPLOAD) + "/" + fileName;

        CopyObjectRequest copyObjRequest = new CopyObjectRequest(
                awsS3Properties.getS3().getBucket(), from, awsS3Properties.getS3().getBucket(), to
        );

        s3Client.copyObject(copyObjRequest);

        DeleteObjectRequest deleteObjRequest = new DeleteObjectRequest(awsS3Properties.getS3().getBucket(), from);
        s3Client.deleteObject(deleteObjRequest);
    }

    @Override
    public void deleteFile(String realFileName, String contentType) {

        String path = removeBucket(awsS3Properties.getImgs().getTemporary()) + "/" + realFileName;

        DeleteObjectRequest deleteObjRequest = new DeleteObjectRequest(awsS3Properties.getS3().getBucket(), path);
        s3Client.deleteObject(deleteObjRequest);
    }

    private String removeBucket(String sourcePath) {
        return sourcePath.substring(sourcePath.indexOf('/') + 1);
    }
}
