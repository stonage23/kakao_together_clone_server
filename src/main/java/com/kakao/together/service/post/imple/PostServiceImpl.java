package com.kakao.together.service.post.imple;

import com.kakao.together.api.htmlparser.JsoupHtmlParser;
import com.kakao.together.api.htmlparser.RawTag;
import com.kakao.together.controller.dto.ContentDto.ContentCommand;
import com.kakao.together.controller.dto.ContentDto.ImageContentCommand;
import com.kakao.together.controller.dto.ContentDto.SubtitleContentCommand;
import com.kakao.together.controller.dto.ContentDto.TextContentCommand;
import com.kakao.together.controller.fundraising.dto.FundraisingDto.EditFundraisingRequest;
import com.kakao.together.controller.image.dto.ImageCommand;
import com.kakao.together.controller.post.dto.PostCommand;
import com.kakao.together.domain.entity.content.ContentType;
import com.kakao.together.domain.entity.content.extend.ImageContent;
import com.kakao.together.domain.entity.image.FileInfo;
import com.kakao.together.domain.entity.post.Post;
import com.kakao.together.domain.entity.post.PostType;
import com.kakao.together.domain.repository.ContentRepository;
import com.kakao.together.domain.repository.FileInfoRepository;
import com.kakao.together.domain.repository.PostRepository;
import com.kakao.together.exception.CustomException;
import com.kakao.together.exception.ErrorCode;
import com.kakao.together.mapper.TagMapper;
import com.kakao.together.service.file.FileStorageService;
import com.kakao.together.service.file.impl.FilePathResolver;
import com.kakao.together.service.post.PostService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class PostServiceImpl implements PostService {

    private final PostRepository postRepository;
    private final ContentRepository contentRepository;
    private final FileInfoRepository fileInfoRepository;
    private final FileStorageService fileStorageService;
    private final FilePathResolver filePathResolver;

    @Override
    public Post createPost(Post post) {
        return postRepository.save(post);
    }

    @Override
    public Post findPostById(Long postId) {
        return postRepository.findById(postId)
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_ENTITY, "해당 id에 해당하는 POST 엔티티가 존재하지 않습니다."));
    }

    @Override
    @Transactional
    public Long buildPost(EditFundraisingRequest request) {

        PostCommand postCommand;
        List<ContentCommand> contents = extractContentsFromHtml(request.getHtml());

        if (request.getPostId() == null) {
            postCommand = PostCommand.builder()
                    .contents(contents)
                    .build();
        } else {
            beforeUpdatePost(request.getHtml(), request.getPostId());
            postCommand = PostCommand.builder()
                    .postId(request.getPostId())
                    .contents(contents)
                    .build();
        }

        return buildPost(postCommand);
    }

    private Long buildPost(PostCommand postCommand) {
        Post post = null;

        if (postCommand.getPostId() == null) {
            Post buildPost = Post.builder()
                    .type(PostType.STORY)
                    .build();
            post = postRepository.save(buildPost);
        } else {
            post = postRepository.findById(postCommand.getPostId()).orElseThrow(
                    () -> new CustomException(ErrorCode.NOT_FOUND_ENTITY, "요청한 엔티티가 존재하지 않습니다; postId: " + postCommand.getPostId()));
        }

        final Post finalCreatedPost = post;

        postCommand.getContents().forEach(contentCommand -> {
            if (contentCommand instanceof ImageContentCommand imageContentCommand) {
                FileInfo image = fileInfoRepository.findById(imageContentCommand.getImageId())
                        .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_ENTITY));
                contentRepository.save(imageContentCommand.toEntity(imageContentCommand.getPost(), image));
            } else if (contentCommand instanceof TextContentCommand textContentCommand) {
                contentRepository.save(textContentCommand.toEntity(finalCreatedPost));
            } else if (contentCommand instanceof SubtitleContentCommand subtitleContentCommand) {
                contentRepository.save(subtitleContentCommand.toEntity(finalCreatedPost));
            } else throw new CustomException(ErrorCode.NOT_PERMITTED_CONDITION);
        });

        return finalCreatedPost.getId();
    }

    private void checkImageContentsChange(String html, Long postId) {
        Set<Long> removedIds = extractRemovedImageIds(html, postId);
        deleteImagesByIds(removedIds);
    }

    @Override
    @Transactional
    public void beforeUpdatePost(String html, Long postId) {
        checkImageContentsChange(html, postId);
        clearPost(postId);
    }

    private void clearPost(Long postId) {
        Post post = postRepository.findById(postId).orElseThrow(
                () -> new CustomException(ErrorCode.NOT_FOUND_ENTITY, "요청한 엔티티가 존재하지 않습니다; postId: " + postId));

        contentRepository.deleteAll(post.getContents());
    }

    private Set<Long> extractRemovedImageIds(String html, Long postId) {
        Set<Long> currentIds = JsoupHtmlParser.extractTagsFromBody(html, "img").stream().map(imgTag -> Long.valueOf(imgTag.getAttributes().get("imageId")))
                .collect(Collectors.toSet());

        Set<Long> previousIds = contentRepository.findAllImageContentByPostId(postId).stream().map(imageContent -> imageContent.getImage().getId())
                .collect(Collectors.toSet());

        previousIds.removeAll(currentIds);
        return previousIds;
    }

    private void deleteImagesByIds(Set<Long> imageIds) {
        for (Long id : imageIds) {
            ImageContent imgContent = contentRepository.findByImageId(id);
            FileInfo image = imgContent.getImage();
            try {
                fileStorageService.deleteFile(image.generateFilename(), image.getContentType());
            } catch (IOException e) {
                throw new CustomException(ErrorCode.FAILED_DELETE_FILE);
            }
            contentRepository.deleteById(imgContent.getId());
            fileInfoRepository.deleteById(image.getId());
        }
    }

    @Override
    @Transactional
    public List<ContentCommand> extractContentsFromHtml(String html) {
        List<RawTag> tags = JsoupHtmlParser.parseBodyFragment(html);

        // TODO 이 로직 좀 이상함
        tags.stream().filter(tag -> Objects.equals(tag.getTagName(), "img"))
                .forEach(this::createImageIfSrcNotExist);

        return tagsToContents(tags);
    }

    private List<ContentCommand> tagsToContents(List<RawTag> tags) {

        AtomicInteger order = new AtomicInteger(0);
        List<ContentCommand> contentCommands = new ArrayList<>();

        tags.forEach(tag -> {
                    int currentOrder = order.getAndIncrement();
                    ContentType type = ContentType.fromTag(tag.getTagName());
                    switch (type) {
                        case SUBTITLE -> {
                            SubtitleContentCommand subtitleContentCommand = TagMapper.toSubtitleContentCommand(tag);
                            subtitleContentCommand.setOrder(currentOrder);
                            contentCommands.add(subtitleContentCommand);
                        }
                        case TEXT -> {
                            TextContentCommand textContentCommand = TagMapper.toTextContentCommand(tag);
                            textContentCommand.setOrder(currentOrder);
                            contentCommands.add(textContentCommand);
                        }
                        case IMAGE -> {
                            ImageContentCommand imageContentCommand = TagMapper.toImageContentCommand(tag);
                            imageContentCommand.setOrder(currentOrder);
                            contentCommands.add(imageContentCommand);
                        }
                        default -> throw new CustomException(ErrorCode.NOT_VALID_TAG);
                    }
                }
        );

        return contentCommands;
    }

    private void createImageIfSrcNotExist(RawTag imgTag) {

        Optional<FileInfo> image = fileInfoRepository.findBySavedName(imgTag.getAttributes().get("savedName"));

        if (image.isEmpty()) {
            ImageCommand imageCommand = TagMapper.toImageCommand(imgTag);
            fileInfoRepository.save(imageCommand.toEntity());
        }
    }
}
