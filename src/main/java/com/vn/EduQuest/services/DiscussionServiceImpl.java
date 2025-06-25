package com.vn.EduQuest.services;

import com.vn.EduQuest.entities.Discussion;
import com.vn.EduQuest.entities.DiscussionComment;
import com.vn.EduQuest.entities.Exercise;
import com.vn.EduQuest.entities.User;
import com.vn.EduQuest.enums.StatusCode;
import com.vn.EduQuest.exceptions.CustomException;
import com.vn.EduQuest.mapper.DiscussionCommentMapper;
import com.vn.EduQuest.mapper.DiscussionMapper;
import com.vn.EduQuest.payload.request.discussion.DiscussionCommentRequest;
import com.vn.EduQuest.payload.request.discussion.DiscussionRequest;
import com.vn.EduQuest.payload.response.discussion.CommentResponse;
import com.vn.EduQuest.payload.response.discussion.DiscussionResponse;
import com.vn.EduQuest.payload.response.discussion.DiscussionUpdateRequest;
import com.vn.EduQuest.payload.response.discussion.LikeResponse;
import com.vn.EduQuest.repositories.DiscussionCommentRepository;
import com.vn.EduQuest.repositories.DiscussionRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

@Slf4j
@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class DiscussionServiceImpl implements DiscussionService {
    ExerciseService exerciseService;
    UserService userService;
    DiscussionMapper discussionMapper;
    DiscussionRepository discussionRepository;
    DiscussionCommentRepository discussionCommentRepository;
    DiscussionCommentMapper discussionCommentMapper;

    @Override
    public DiscussionResponse createDiscussion(Long userId, DiscussionRequest request) throws CustomException {
        Exercise exercise = exerciseService.getExerciseById(request.getExerciseId());
        User user = userService.getUserById(userId);
        if (!exerciseService.isExpired(request.getExerciseId())) {
            throw new CustomException(StatusCode.EXERCISE_NOT_EXPIRED_YET);
        }
        Discussion discussion = discussionMapper.toEntity(user, request.getContent(), exercise);
        var discussionSaved = discussionRepository.save(discussion);
        var response = discussionMapper.toResponse(discussionSaved);
        response.setAvatarUrl(formatAvatarUrl(user.getAvatarUrl()));
        if (response.getAvatarUrl() != null) {
            discussionSaved.getCreatedBy().setAvatarUrl(formatAvatarUrl(response.getAvatarUrl()));
        }
        return response;
    }

    @Override
    public boolean deleteDiscussion(Long discussionId, Long userId) throws CustomException {
        Discussion discussion = getDiscussionById(discussionId);
        if (!Objects.equals(discussion.getCreatedBy().getId(), userId)) {
            throw new CustomException(StatusCode.NOT_MATCH, "User", "discussion creator");
        }
        discussionRepository.delete(discussion);
        return true;
    }

    @Override
    public DiscussionResponse updateDiscussion(Long discussionId, Long userId, DiscussionUpdateRequest request) throws CustomException {
        Discussion discussion = getDiscussionById(discussionId);
        if (!Objects.equals(discussion.getCreatedBy().getId(), userId)) {
            throw new CustomException(StatusCode.NOT_MATCH, "User", "discussion creator");
        }
        discussion.setContent(request.getContent());
        var updatedDiscussion = discussionRepository.save(discussion);
        var response = discussionMapper.toResponse(updatedDiscussion);
        response.setAvatarUrl(formatAvatarUrl(updatedDiscussion.getCreatedBy().getAvatarUrl()));
        if (response.getAvatarUrl() != null) {
            updatedDiscussion.getCreatedBy().setAvatarUrl(formatAvatarUrl(response.getAvatarUrl()));
        }
        return response;
    }

    @Override
    public Discussion getDiscussionById(Long discussionId) throws CustomException {
        return discussionRepository.findById(discussionId)
                .orElseThrow(() -> new CustomException(StatusCode.NOT_FOUND, "discussion", discussionId));
    }

    @Override
    public List<DiscussionResponse> getAllDiscussionsByExerciseId(Long exerciseId) throws CustomException {
        Exercise exercise = exerciseService.getExerciseById(exerciseId);
        List<Discussion> discussions = discussionRepository.findByExercise_IdOrderByCreatedAtDesc(exercise.getId());
        return discussions.stream()
                .map(discussion -> {
                    DiscussionResponse response = discussionMapper.toResponse(discussion);
                    if (discussion.getCreatedBy() != null) {
                        response.setAvatarUrl(formatAvatarUrl(discussion.getCreatedBy().getAvatarUrl()));
                    }
                    return response;
                })
                .toList();
    }

    @Override
    public CommentResponse saveComment(DiscussionCommentRequest request) throws CustomException {
        Discussion discussion = getDiscussionById(request.getDiscussionId());
        User user = userService.getUserById(request.getCreatedBy());
        var comment = discussionCommentMapper.toEntity(request.getContent(), user, discussion);
        var savedComment = discussionCommentRepository.save(comment);
        var response = discussionCommentMapper.toResponse(savedComment);
        response.setCreatedByAvatar(formatAvatarUrl(user.getAvatarUrl()));
        return response;
    }

    @Override
    public List<CommentResponse> getAllCommentsByDiscussionId(Long discussionId) throws CustomException {
        Discussion discussion = getDiscussionById(discussionId);
        return discussionCommentRepository.findByDiscussion(discussion)
                .stream()
                .map(comment -> {
                    CommentResponse response = discussionCommentMapper.toResponse(comment);
                    response.setCreatedByAvatar(formatAvatarUrl(comment.getCreatedBy().getAvatarUrl()));
                    return response;
                })
                .toList();
    }

    @Override
    public LikeResponse voteDiscussion(Long discussionCommentId, boolean isUpvote) throws CustomException {
        DiscussionComment discussionComment = discussionCommentRepository.findById(discussionCommentId)
                .orElseThrow(() -> new CustomException(StatusCode.NOT_FOUND, "discussion comment", discussionCommentId));
        int currentVoteCount = discussionComment.getVoteCount();
        discussionComment.setVoteCount(++currentVoteCount);
        discussionCommentRepository.save(discussionComment);
        return LikeResponse.builder()
                .liked(true)
                .likeCount(discussionComment.getVoteCount())
                .discussionCommentId(discussionComment.getId())
                .type("LIKE")
                .build();
    }

    @Override
    public Long getDiscussionIdByCommentId(long discussionCommentId) throws CustomException {
        DiscussionComment discussionComment = discussionCommentRepository.findById(discussionCommentId)
                .orElseThrow(() -> new CustomException(StatusCode.NOT_FOUND, "discussion comment", discussionCommentId));
        return discussionComment.getDiscussion().getId();
    }

    private String formatAvatarUrl(String avatarUrl) {
        if (avatarUrl == null || avatarUrl.isBlank()) return null;
        if (avatarUrl.startsWith("http://") || avatarUrl.startsWith("https://")) {
            return avatarUrl;
        }
        return "http://localhost:8080" + (avatarUrl.startsWith("/") ? avatarUrl : "/" + avatarUrl);
    }
}
