package com.kakao.together.event;

import com.kakao.together.file.repository.FileInfoRepository;
import com.kakao.together.exception.CustomException;
import com.kakao.together.exception.ErrorCode;
import com.kakao.together.exception.file.FileException;
import com.kakao.together.file.resolver.ResourceUrlResolver;
import com.kakao.together.file.storage.FileStorageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
@RequiredArgsConstructor
@Slf4j
public class PostEventListener {

    private final FileStorageService fileStorageService;
    private final FileInfoRepository fileInfoRepository;
    private final ResourceUrlResolver resourceUrlResolver;

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handlePostProcessed(PostProcessCompleteEvent event) {
        try {
            event.getImageIdList().forEach(imageId ->
                    fileInfoRepository.findById(imageId).ifPresentOrElse(
                            image -> {
                                fileStorageService.moveToStorageUpload(image.getSavedName(), image.getContentType());
                            },
                            () -> log.warn("존재해야 하는 파일 메타데이터가 존재하지 않음. fileInfoId: {}", imageId)
                    )
            );
        } catch (FileException e) {
            log.error("파일 처리도중 발생한 예외로 게시글 작성 실패", e);
            throw new CustomException(ErrorCode.FILE_HANDLING_EXCEPTION);
        }
    }
}
