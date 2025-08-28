package com.kakao.together.service.post.imple;

import com.kakao.together.api.htmlparser.JsoupHtmlParser;
import com.kakao.together.api.htmlparser.RawTag;
import com.kakao.together.controller.dto.ContentDto;
import com.kakao.together.controller.dto.ContentDto.ContentCommand;
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
import lombok.NonNull;
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
        return postRepository.findById(postId).orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_ENTITY, "해당 id에 해당하는 POST 엔티티가 존재하지 않습니다."));
    }

    @Override
    public String postToHtml(@NonNull Post post) {
        StringBuilder html = new StringBuilder();
//        for (Content content : post.getContents()) {
//            if (content instanceof SubTitleContent subTitleContent) {
//                html.append("<").append(ContentType.SUBTITLE.getTag()).append(">");
//                html.append(subTitleContent.getSubtitle());
//                html.append("</").append(ContentType.SUBTITLE.getTag()).append(">");
//            } else if (content instanceof TextContent textContent) {
//                html.append("<").append(ContentType.TEXT.getTag()).append(">");
//                html.append(textContent.getText());
//                html.append("</").append(ContentType.TEXT.getTag()).append(">");
//            } else if (content instanceof ImageContent imageContent) {
//                FileInfo image = imageContent.getImage();
//                String url = filePathResolver.resolveUploadPath(image.generateFilename().toString(), image.getContentType()).toString();
//                html.append("<").append(ContentType.IMAGE.getTag()).append(" src=\"");
//                html.append(url);
//                html.append("\" ");
//                html.append()
//                html.append("/>");
//            } else throw new CustomException(ErrorCode.NOT_VALID_TAG, "허용 외 타입의 콘텐츠가 DB에 저장되어 있어 HTML 구성 실패");
//            html.append("\n");
//        }
        return html.toString();
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

        postCommand.getContents().forEach(contentCommand ->
                contentRepository.save(contentCommand.toEntity(finalCreatedPost)));

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
                            ContentDto.SubtitleContentCommand subtitleContentCommand = TagMapper.toSubtitleContentCommand(tag);
                            subtitleContentCommand.setOrder(currentOrder);
                            contentCommands.add(subtitleContentCommand);
                        }
                        case TEXT -> {
                            ContentDto.TextContentCommand textContentCommand = TagMapper.toTextContentCommand(tag);
                            textContentCommand.setOrder(currentOrder);
                            contentCommands.add(textContentCommand);
                        }
                        case IMAGE -> {
                            ContentDto.ImageContentCommand imageContentCommand = TagMapper.toImageContentCommand(tag);
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

        Optional<FileInfo> image = fileInfoRepository.findByUrl(imgTag.getAttributes().get("src"));

        if (image.isEmpty()) {
            ImageCommand imageCommand = TagMapper.toImageCommand(imgTag);
            fileInfoRepository.save(imageCommand.toEntity());
        }
    }
}
