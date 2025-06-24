package com.vn.EduQuest.mapper;

import com.vn.EduQuest.entities.Discussion;
import com.vn.EduQuest.entities.DiscussionComment;
import com.vn.EduQuest.entities.User;
import com.vn.EduQuest.payload.response.discussion.CommentResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface DiscussionCommentMapper {

    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    DiscussionComment toEntity(String content, User createdBy, Discussion discussion);

    @Mapping(target = "discussionId", source = "discussionComment.discussion.id")
    @Mapping(target = "createdByAvatar", expression = "java(formatAvatarUrl(discussionComment.getCreatedBy().getAvatarUrl()))")
    @Mapping(target = "createdByName", source = "discussionComment.createdBy.name")
    CommentResponse toResponse(DiscussionComment discussionComment);

    default String formatAvatarUrl(String avatarUrl) {
        if (avatarUrl == null || avatarUrl.isBlank()) return null;
        if (avatarUrl.startsWith("http://") || avatarUrl.startsWith("https://")) {
            return avatarUrl;
        }
        return "http://localhost:8080" + (avatarUrl.startsWith("/") ? avatarUrl : "/" + avatarUrl);
    }
}
