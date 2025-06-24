package com.vn.EduQuest.mapper;

import com.vn.EduQuest.entities.Discussion;
import com.vn.EduQuest.entities.Exercise;
import com.vn.EduQuest.entities.User;
import com.vn.EduQuest.payload.response.discussion.DiscussionResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface DiscussionMapper {
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "createdBy", source = "user")
    Discussion toEntity(User user, String content, Exercise exercise);

    @Mapping(target = "createdByName", source = "createdBy.name")
    DiscussionResponse toResponse(Discussion discussion);
}
