package com.kakao.together.service.post.imple;

import com.kakao.together.controller.fundraising.dto.FundraisingDto.EditFundraisingRequest;
import com.kakao.together.controller.image.dto.ImageCommand;
import com.kakao.together.controller.post.dto.ContentDto.ContentCommand;
import com.kakao.together.controller.post.dto.ContentDto.ImageContentCommand;
import com.kakao.together.controller.post.dto.ContentDto.SubtitleContentCommand;
import com.kakao.together.controller.post.dto.ContentDto.TextContentCommand;
import com.kakao.together.controller.post.dto.PostCommand;
import com.kakao.together.controller.post.dto.RawTag;
import com.kakao.together.domain.entity.content.Content;
import com.kakao.together.domain.entity.content.ContentType;
import com.kakao.together.domain.entity.content.extend.ImageContent;
import com.kakao.together.domain.entity.content.extend.SubTitleContent;
import com.kakao.together.domain.entity.content.extend.TextContent;
import com.kakao.together.file.domain.FileInfo;
import com.kakao.together.domain.entity.post.Post;
import com.kakao.together.domain.entity.post.PostType;
import com.kakao.together.domain.repository.ContentRepository;
import com.kakao.together.file.repository.FileInfoRepository;
import com.kakao.together.domain.repository.PostRepository;
import com.kakao.together.event.PostProcessCompleteEvent;
import com.kakao.together.exception.CustomException;
import com.kakao.together.exception.ErrorCode;
import com.kakao.together.helper.JsoupHtmlParser;
import com.kakao.together.mapper.TagMapper;
import com.kakao.together.file.resolver.ResourceUrlResolver;
import com.kakao.together.service.post.PostService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class PostServiceImpl implements PostService {

    private final PostRepository postRepository;
    private final ContentRepository contentRepository;
    private final FileInfoRepository fileInfoRepository;
    private final ResourceUrlResolver resourceUrlResolver;
    private final ApplicationEventPublisher eventPublisher;

    @Override
    public Post findPostById(Long postId) {
        return postRepository.findById(postId)
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_ENTITY, "해당 id에 해당하는 POST 엔티티가 존재하지 않습니다."));
    }

    @Override
    @Transactional
    public Long createPost(EditFundraisingRequest request) {

        PostCommand postCommand;
        List<ContentCommand> contents = extractContentsFromHtml(request.getHtml());

        postCommand = PostCommand.builder()
                .contents(contents)
                .build();

        return buildPost(postCommand);
    }

    @Override
    @Transactional
    public void updatePost(EditFundraisingRequest request, Long postId) {
        beforeUpdatePost(request.getHtml(), postId);
        List<ContentCommand> contents = extractContentsFromHtml(request.getHtml());

        PostCommand postCommand = PostCommand.builder()
                .postId(postId)
                .contents(contents)
                .build();

        buildPost(postCommand);
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
        final List<Long> fileInfoIds = new ArrayList<>();

        List<Content> contents = postCommand.getContents().stream()
                .map(contentCommand -> {
            if (contentCommand instanceof ImageContentCommand imageContentCommand) {
                FileInfo image = fileInfoRepository.findById(imageContentCommand.getImageId())
                        .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_ENTITY));
                image.updateStatusToUsed();

                fileInfoIds.add(image.getId());

                return imageContentCommand.toEntity(finalCreatedPost, image);
            } else if (contentCommand instanceof TextContentCommand textContentCommand) {
                return textContentCommand.toEntity(finalCreatedPost);
            } else if (contentCommand instanceof SubtitleContentCommand subtitleContentCommand) {
                return subtitleContentCommand.toEntity(finalCreatedPost);
            } else throw new CustomException(ErrorCode.NOT_PERMITTED_CONDITION);
        }).collect(Collectors.toList());

        finalCreatedPost.updatePost(contents);

        eventPublisher.publishEvent(new PostProcessCompleteEvent(fileInfoIds));
        return finalCreatedPost.getId();
    }

    private void checkImageContentsChange(String html, Long postId) {
        Set<Long> removedIds = extractRemovedImageIds(html, postId);
        deleteImagesByIds(removedIds);

        JsoupHtmlParser.extractTagsFromBody(html, "figure")
                .forEach(figureTag -> {
                    List<RawTag> extractedTag = JsoupHtmlParser.extractTagsFromBody(figureTag.getInnerHtml(), "img");
                    if (extractedTag.size() == 0) return;
                    Long imageId = Long.valueOf(extractedTag.get(0).getAttributes().get("imageid"));
                    FileInfo image = fileInfoRepository.findById(imageId).orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_ENTITY));
                    image.updateStatusToUsed();
                });
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

        contentRepository.deleteBulkByPostId(post.getId());
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

            image.updateStatusToDeleted();
        }

    }

    @Override
    @Transactional
    public List<ContentCommand> extractContentsFromHtml(String html) {
        List<RawTag> tags = JsoupHtmlParser.parseBodyFragment(html);

        tags.stream().filter(tag -> Objects.equals(tag.getTagName(), "img"))
                .forEach(this::createImageIfSrcNotExist);

        return tagsToContents(tags);
    }

    @Override
    public String resolveContent(Long postId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_ENTITY));
        List<Content> contents = post.getContents();

        StringBuilder contentBuilder = new StringBuilder();
        contents.forEach(content -> {
                    if (content instanceof SubTitleContent subTitleContent)
                        contentBuilder.append("<h2>").append(subTitleContent.getSubtitle()).append("</h2>");
                    else if (content instanceof TextContent textContent)
                        contentBuilder.append("<p>").append(textContent.getText()).append("</p>");
                    else if (content instanceof ImageContent imageContent) {
                        FileInfo image = imageContent.getImage();
                        String url = resourceUrlResolver.resolveUploadUrl(image.getSavedName(), image.getContentType());
                        contentBuilder.append("<figure class='image-container'><img alt='image' src='").append(url).append("' imageid='").append(image.getId()).append("'></img></figure>");
                    } else {
                        log.warn("모금 Story내 content가 허용하는 타입 이외 다른 타입을 갖음; contentId: " + content.getId());
                        throw new CustomException(ErrorCode.NOT_VALID_CONTENT, "contentId: " + content.getId());
                    }
                });
        return contentBuilder.toString();
    }

    private List<ContentCommand> tagsToContents(List<RawTag> tags) {

        List<ContentCommand> contentCommands = new ArrayList<>();

        int order = 0;
        StringBuilder paragraphBuilder = new StringBuilder();

        for (RawTag tag : tags) {
            ContentType type = ContentType.fromTag(tag.getTagName());

            if (type == ContentType.TEXT) {
                if (!paragraphBuilder.isEmpty() &&
                !tag.getText().isEmpty())
                    paragraphBuilder.append("\n \n").append(tag.getText());
                else if (!tag.getText().isEmpty())
                    paragraphBuilder.append(tag.getText());
            } else {
                if (!paragraphBuilder.isEmpty()) {
                    contentCommands.add(TagMapper.toTextContentCommand(paragraphBuilder.toString(), order++));
                    paragraphBuilder.setLength(0);
                }

                if (type == ContentType.SUBTITLE) {
                    contentCommands.add(TagMapper.toSubtitleContentCommand(tag, order++));
                } else if (type == ContentType.
                FIGURE) {
                    List<RawTag> extractedTags = JsoupHtmlParser.parseBodyFragment(tag.getInnerHtml());
                    contentCommands.add(TagMapper.toImageContentCommand(extractedTags.get(0), order++));
                } else throw new CustomException(ErrorCode.NOT_VALID_TAG);
            }
        }
        if (!paragraphBuilder.isEmpty()) contentCommands.add(TagMapper.toTextContentCommand(paragraphBuilder.toString(), order));
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
