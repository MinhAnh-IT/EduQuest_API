package com.vn.EduQuest.controllers;

import com.vn.EduQuest.enums.StatusCode;
import com.vn.EduQuest.exceptions.CustomException;
import com.vn.EduQuest.payload.ApiResponse;
import com.vn.EduQuest.payload.request.discussion.DiscussionRequest;
import com.vn.EduQuest.payload.response.discussion.DiscussionUpdateRequest;
import com.vn.EduQuest.security.UserDetailsImpl;
import com.vn.EduQuest.services.DiscussionService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequestMapping("/api/discussions")
public class DiscussionController {
    DiscussionService discussionService;

    @PostMapping()
    public ResponseEntity<?> createDiscussion(
            @RequestBody DiscussionRequest request,
            @AuthenticationPrincipal UserDetailsImpl userDetails) throws CustomException {
        var response = discussionService.createDiscussion(userDetails.getId(), request);
        ApiResponse<?> apiResponse = ApiResponse.builder()
                .code(StatusCode.OK.getCode())
                .message(StatusCode.OK.getMessage())
                .data(response)
                .build();
        return ResponseEntity.ok(apiResponse);
    }

    @GetMapping("/exercises/{exerciseId}")
    public ResponseEntity<?> getDiscussionsByExerciseId(
            @PathVariable Long exerciseId,
            @AuthenticationPrincipal UserDetailsImpl userDetails) throws CustomException {
        var response = discussionService.getAllDiscussionsByExerciseId(exerciseId);
        ApiResponse<?> apiResponse = ApiResponse.builder()
                .code(StatusCode.OK.getCode())
                .message(StatusCode.OK.getMessage())
                .data(response)
                .build();
        return ResponseEntity.ok(apiResponse);
    }

    @DeleteMapping("/{discussionId}")
    public ResponseEntity<?> deleteDiscussion(
            @PathVariable Long discussionId,
            @AuthenticationPrincipal UserDetailsImpl userDetails) throws CustomException {
        var result = discussionService.deleteDiscussion(discussionId, userDetails.getId());
        ApiResponse<?> apiResponse = ApiResponse.builder()
                .code(StatusCode.OK.getCode())
                .message(StatusCode.OK.getMessage())
                .data(result)
                .build();
        return ResponseEntity.ok(apiResponse);
    }

    @PutMapping("/{discussionId}")
    public ResponseEntity<?> updateDiscussion(
            @PathVariable Long discussionId,
            @RequestBody DiscussionUpdateRequest request,
            @AuthenticationPrincipal UserDetailsImpl userDetails) throws CustomException {
        var response = discussionService.updateDiscussion(discussionId, userDetails.getId(), request);
        ApiResponse<?> apiResponse = ApiResponse.builder()
                .code(StatusCode.OK.getCode())
                .message(StatusCode.OK.getMessage())
                .data(response)
                .build();
        return ResponseEntity.ok(apiResponse);
    }

    @GetMapping("/{discussionId}/comments")
    public ResponseEntity<?> getCommentsByDiscussionId(
            @PathVariable Long discussionId,
            @AuthenticationPrincipal UserDetailsImpl userDetails) throws CustomException {
        var response = discussionService.getAllCommentsByDiscussionId(discussionId);
        ApiResponse<?> apiResponse = ApiResponse.builder()
                .code(StatusCode.OK.getCode())
                .message(StatusCode.OK.getMessage())
                .data(response)
                .build();
        return ResponseEntity.ok(apiResponse);
    }
}
