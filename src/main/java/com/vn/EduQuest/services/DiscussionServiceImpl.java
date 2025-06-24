package com.vn.EduQuest.services;

import com.vn.EduQuest.entities.Discussion;
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
import com.vn.EduQuest.repositories.DiscussionCommentRepository;
import com.vn.EduQuest.repositories.DiscussionRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

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
        return discussionMapper.toResponse(discussionSaved);
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
        return discussionMapper.toResponse(updatedDiscussion);
    }

    @Override
    public Discussion getDiscussionById(Long discussionId) throws CustomException {
        return discussionRepository.findById(discussionId)
                .orElseThrow(() -> new CustomException(StatusCode.NOT_FOUND, "discussion", discussionId));
    }

    @Override
    public List<DiscussionResponse> getAllDiscussionsByExerciseId(Long exerciseId) throws CustomException {
        Exercise exercise = exerciseService.getExerciseById(exerciseId);
        List<Discussion> discussions = discussionRepository.findByExercise_Id(exercise.getId());
        return discussions.stream()
                .map(discussionMapper::toResponse)
                .toList();
    }

    @Override
    public CommentResponse saveComment(DiscussionCommentRequest request) throws CustomException {
        Discussion discussion = getDiscussionById(request.getDiscussionId());
        User user = userService.getUserById(request.getCreatedBy());
        var comment = discussionCommentMapper.toEntity(request.getContent(), user, discussion);
        var savedComment = discussionCommentRepository.save(comment);
        return discussionCommentMapper.toResponse(savedComment);
    }

    @Override
    public List<CommentResponse> getAllCommentsByDiscussionId(Long discussionId) throws CustomException {
        Discussion discussion = getDiscussionById(discussionId);
        return discussionCommentRepository.findByDiscussion(discussion)
                .stream()
                .map(discussionCommentMapper::toResponse)
                .toList();
    }
}
