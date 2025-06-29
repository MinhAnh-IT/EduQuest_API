package com.vn.EduQuest.controllers;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.vn.EduQuest.enums.StatusCode;
import com.vn.EduQuest.exceptions.CustomException;
import com.vn.EduQuest.payload.ApiResponse;
import com.vn.EduQuest.payload.response.Exercise.ExerciseResponse;
import com.vn.EduQuest.payload.response.Exercise.ExerciseResultsResponse;
import com.vn.EduQuest.payload.response.Exercise.InstructorExerciseResponse;
import com.vn.EduQuest.security.UserDetailsImpl;
import com.vn.EduQuest.services.ExerciseService;
import com.vn.EduQuest.services.ParticipationService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/exam")
@RequiredArgsConstructor
public class ExerciseController {
    private final ExerciseService exerciseService;
    private final ParticipationService participationService;

    @GetMapping("/exercises/{classId}")
    public ResponseEntity<?> getExercisesForStudent(@AuthenticationPrincipal UserDetailsImpl userDetails,@PathVariable Long classId
    ) throws CustomException {
        List<ExerciseResponse> exercises = exerciseService.getExercisesForStudent(userDetails.getId(), classId);
        ApiResponse<?> response = ApiResponse.<List<ExerciseResponse>>builder()
                .code(StatusCode.OK.getCode())
                .message(StatusCode.OK.getMessage())
                .data(exercises)
                .build();
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{exerciseId}/results")
    public ResponseEntity<?> getExerciseResults(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @PathVariable Long exerciseId) throws CustomException {
        
        ExerciseResultsResponse result = participationService.getExerciseResults(userDetails.getId(), exerciseId);
        
        ApiResponse<?> response = ApiResponse.<ExerciseResultsResponse>builder()
                .code(StatusCode.OK.getCode())
                .message("Successfully retrieved exercise results")
                .data(result)
                .build();
        return ResponseEntity.ok(response);
    }

    @GetMapping("/instructor/classes/{classId}/exercises")
    public ResponseEntity<?> getInstructorExercisesByClass(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @PathVariable Long classId) throws CustomException {
        
        List<InstructorExerciseResponse> result = exerciseService.getInstructorExercisesByClass(userDetails.getId(), classId);
        
        String message = result.isEmpty() 
            ? "No exercises found for this class" 
            : "Successfully retrieved exercises for class";
            
        ApiResponse<?> response = ApiResponse.<List<InstructorExerciseResponse>>builder()
                .code(StatusCode.OK.getCode())
                .message(message)
                .data(result)
                .build();
        return ResponseEntity.ok(response);
    }

    @GetMapping("/instructor/my-exercises")
    public ResponseEntity<?> getInstructorExercises(
            @AuthenticationPrincipal UserDetailsImpl userDetails) throws CustomException {
        
        List<InstructorExerciseResponse> result = exerciseService.getInstructorExercises(userDetails.getId());
        
        String message = result.isEmpty() 
            ? "No exercises found" 
            : "Successfully retrieved instructor exercises";
            
        ApiResponse<?> response = ApiResponse.<List<InstructorExerciseResponse>>builder()
                .code(StatusCode.OK.getCode())
                .message(message)
                .data(result)
                .build();
        return ResponseEntity.ok(response);
    }
}