package com.vn.EduQuest.mapper;

import com.vn.EduQuest.entities.Discussion;
import com.vn.EduQuest.entities.Exercise;
import com.vn.EduQuest.entities.User;
import com.vn.EduQuest.payload.response.discussion.DiscussionResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

@Mapper(componentModel = "spring")
public interface DiscussionMapper {
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "createdBy", source = "user")
    Discussion toEntity(User user, String content, Exercise exercise);

    @Mappings
            ({@Mapping(target = "createdById", source = "createdBy.id"),
                    @Mapping(target = "createdByName", source = "createdBy.name"),
                    @Mapping(target = "avatarUrl", ignore = true)
            })

    DiscussionResponse toResponse(Discussion discussion);
}
