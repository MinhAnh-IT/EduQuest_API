package com.vn.EduQuest.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.vn.EduQuest.enums.StatusCode;
import com.vn.EduQuest.exceptions.CustomException;
import com.vn.EduQuest.payload.ApiResponse;
import com.vn.EduQuest.payload.request.participation.SubmissionExamRequest;
import com.vn.EduQuest.security.UserDetailsImpl;
import com.vn.EduQuest.services.ParticipationService;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequestMapping("/api/participations")
public class ParticipationController {

    ParticipationService participationService;

    @PostMapping("/exercises/{exerciseId}/start")
    public ResponseEntity<?> startExam(
            @PathVariable Long exerciseId,
            @AuthenticationPrincipal UserDetailsImpl userDetails) throws Exception {
        var result = participationService.startExam(exerciseId, userDetails.getId());
        ApiResponse<?> response = ApiResponse.builder()
                .code(StatusCode.OK.getCode())
                .message(StatusCode.OK.getMessage())
                .data(result)
                .build();
        return ResponseEntity.ok(response);
    }

    @PostMapping("/exercises/submit")
    public ResponseEntity<?> submitAnswer(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @RequestBody SubmissionExamRequest submissionExamRequest) throws CustomException {
        var result = participationService.submitAnswer(userDetails.getId(), submissionExamRequest);
        ApiResponse<?> response = ApiResponse.builder()
                .code(StatusCode.OK.getCode())
                .message(StatusCode.OK.getMessage())
                .data(result)
                .build();
        return ResponseEntity.ok(response);
    }

    @GetMapping("/exercises/{exerciseId}/result")
    public ResponseEntity<?> getExerciseResults(
            @PathVariable Long exerciseId,
            @AuthenticationPrincipal UserDetailsImpl userDetails) throws CustomException {
        var result = participationService.getResult(userDetails.getStudent().getId(), exerciseId);
        ApiResponse<?> response = ApiResponse.builder()
                .code(StatusCode.OK.getCode())
                .message(StatusCode.OK.getMessage())
                .data(result)
                .build();
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{participationId}/student-detail")
    public ResponseEntity<?> getStudentTestDetail(
            @PathVariable Long participationId,
            @AuthenticationPrincipal UserDetailsImpl userDetails) throws CustomException {
        var result = participationService.getStudentTestDetail(participationId);
        ApiResponse<?> response = ApiResponse.builder()
                .code(StatusCode.OK.getCode())
                .message("Successfully retrieved student test detail")
                .data(result)
                .build();
        return ResponseEntity.ok(response);
    }
}
