package com.vn.EduQuest.payload.request.discussion;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class DiscussionCommentRequest {
    Long discussionId;
    String content;
    Long createdBy;
}
