package com.vn.EduQuest.services;

import com.vn.EduQuest.entities.Discussion;
import com.vn.EduQuest.exceptions.CustomException;
import com.vn.EduQuest.payload.request.discussion.DiscussionCommentRequest;
import com.vn.EduQuest.payload.request.discussion.DiscussionRequest;
import com.vn.EduQuest.payload.response.discussion.CommentResponse;
import com.vn.EduQuest.payload.response.discussion.DiscussionResponse;
import com.vn.EduQuest.payload.response.discussion.DiscussionUpdateRequest;

import java.util.List;

public interface DiscussionService {
    DiscussionResponse createDiscussion(Long userId, DiscussionRequest request) throws CustomException;
    boolean deleteDiscussion(Long discussionId, Long userId) throws CustomException;
    DiscussionResponse updateDiscussion(Long discussionId, Long userId, DiscussionUpdateRequest request) throws CustomException;
    Discussion getDiscussionById(Long discussionId) throws CustomException;
    List<DiscussionResponse> getAllDiscussionsByExerciseId(Long exerciseId) throws CustomException;
    CommentResponse saveComment(DiscussionCommentRequest request) throws CustomException;
    List<CommentResponse> getAllCommentsByDiscussionId(Long discussionId) throws CustomException;
}
