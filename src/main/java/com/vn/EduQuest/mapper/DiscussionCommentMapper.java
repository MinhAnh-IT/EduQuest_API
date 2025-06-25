package com.vn.EduQuest.mapper;

import com.vn.EduQuest.entities.Discussion;
import com.vn.EduQuest.entities.DiscussionComment;
import com.vn.EduQuest.entities.User;
import com.vn.EduQuest.payload.response.discussion.CommentResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface DiscussionCommentMapper {

    @Mapping(target = "voteCount", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "discussion", source = "discussion")
    @Mapping(target = "createdBy", source = "createdBy")
    @Mapping(target = "content", source = "content")
    @Mapping(target = "createdAt", ignore = true)
    DiscussionComment toEntity(String content, User createdBy, Discussion discussion);

    @Mapping(target = "discussionId", source = "discussionComment.discussion.id")
    @Mapping(target = "createdByAvatar", ignore = true)
    @Mapping(target = "createdByName", source = "discussionComment.createdBy.name")
    @Mapping(target = "createdBy", source = "discussionComment.createdBy.id")
    @Mapping(target = "createdAt", source = "discussionComment.createdAt")
    CommentResponse toResponse(DiscussionComment discussionComment);
}
