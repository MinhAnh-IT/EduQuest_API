package com.vn.EduQuest.controllers;

import com.vn.EduQuest.exceptions.CustomException;
import com.vn.EduQuest.payload.request.discussion.DiscussionCommentRequest;
import com.vn.EduQuest.payload.request.discussion.LikeRequest;
import com.vn.EduQuest.payload.response.discussion.CommentResponse;
import com.vn.EduQuest.payload.response.discussion.LikeResponse;
import com.vn.EduQuest.services.DiscussionService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

@Slf4j
@Controller
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class DiscussionWebSocketController {

    SimpMessagingTemplate messagingTemplate;

    DiscussionService discussionService;

    @MessageMapping("/discussion.comment")
    public void handleNewComment(DiscussionCommentRequest message) throws CustomException {
        CommentResponse saved = discussionService.saveComment(message);
        messagingTemplate.convertAndSend(
                "/topic/discussion/" + message.getDiscussionId(),
                saved
        );
    }

    @MessageMapping("/discussion.like")
    public void handleLike(@Payload LikeRequest likeRequest) throws CustomException {
        LikeResponse likeResponse = discussionService.voteDiscussion(
                likeRequest.getDiscussionCommentId(),
                likeRequest.isUpvote()
        );
        Long discussionId = discussionService.getDiscussionIdByCommentId(likeRequest.getDiscussionCommentId());
        messagingTemplate.convertAndSend(
                "/topic/discussion/" + discussionId,
                likeResponse
        );
    }
}
