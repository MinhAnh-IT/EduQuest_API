package com.vn.EduQuest.payload.response.discussion;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class LikeResponse {
    String type;
    boolean liked;
    int likeCount;
    long discussionCommentId;
}
