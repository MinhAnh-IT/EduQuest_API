package com.vn.EduQuest.controllers;

import com.vn.EduQuest.exceptions.CustomException;
import com.vn.EduQuest.payload.request.discussion.DiscussionCommentRequest;
import com.vn.EduQuest.payload.response.discussion.CommentResponse;
import com.vn.EduQuest.services.DiscussionService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

@Controller
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class DiscussionWebSocketController {

    SimpMessagingTemplate messagingTemplate;

    DiscussionService discussionService;

    @MessageMapping("/discussion.comment")
    public void handleNewComment(DiscussionCommentRequest message) throws CustomException {

        CommentResponse saved = discussionService.saveComment(message);

        // 2. Broadcast đến tất cả client trong room discussionId
        messagingTemplate.convertAndSend(
                "/topic/discussion/" + message.getDiscussionId(),
                saved
        );
    }
}
