package com.vn.EduQuest.controllers;

import java.util.List;

import com.vn.EduQuest.payload.request.exercise.ExerciseRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import com.vn.EduQuest.enums.StatusCode;
import com.vn.EduQuest.exceptions.CustomException;
import com.vn.EduQuest.payload.ApiResponse;
<<<<<<< HEAD
import com.vn.EduQuest.payload.response.Exercise.ExerciseResponse;
import com.vn.EduQuest.payload.response.Exercise.ExerciseResultsResponse;
import com.vn.EduQuest.payload.response.Exercise.InstructorExerciseResponse;
=======
import com.vn.EduQuest.payload.response.exercise.ExerciseResponse;
import com.vn.EduQuest.payload.response.exercise.ExerciseResultsResponse;
>>>>>>> ee16b8f (imp api for exercise service)
import com.vn.EduQuest.security.UserDetailsImpl;
import com.vn.EduQuest.services.ExerciseService;
import com.vn.EduQuest.services.ParticipationService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/exercises")
@RequiredArgsConstructor
public class ExerciseController {
    private final ExerciseService exerciseService;
    private final ParticipationService participationService;

    @GetMapping("/{classId}")
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
                .message(StatusCode.OK.getMessage())
                .data(result)
                .build();
        return ResponseEntity.ok(response);
    }

<<<<<<< HEAD
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
=======
    @GetMapping()
    public ResponseEntity<?> getAllExercisesForTeacher(@AuthenticationPrincipal UserDetailsImpl userDetails) throws CustomException {
        var results = exerciseService.getAllExercisesForTeacher(userDetails.getId());
        ApiResponse<?> response = ApiResponse.builder()
                .code(StatusCode.OK.getCode())
                .message(StatusCode.OK.getMessage())
                .data(results)
                .build();
        return ResponseEntity.ok(response);
    }

    @GetMapping("/class/{classId}")
    public ResponseEntity<?> getExerciseByClassId(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @PathVariable Long classId) throws CustomException {
        var result = exerciseService.getExercisesByClassIdForTeacher(userDetails.getId(), classId);

        ApiResponse<?> response = ApiResponse.builder()
                .code(StatusCode.OK.getCode())
                .message(StatusCode.OK.getMessage())
>>>>>>> ee16b8f (imp api for exercise service)
                .data(result)
                .build();
        return ResponseEntity.ok(response);
    }

<<<<<<< HEAD
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
=======
    @GetMapping("/detail/{exerciseId}")
    public ResponseEntity<?> getExerciseDetailForTeacher(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @PathVariable Long exerciseId) throws CustomException {
        var result = exerciseService.getExerciseDetailForTeacher(userDetails.getId(), exerciseId);

        ApiResponse<?> response = ApiResponse.builder()
                .code(StatusCode.OK.getCode())
                .message(StatusCode.OK.getMessage())
>>>>>>> ee16b8f (imp api for exercise service)
                .data(result)
                .build();
        return ResponseEntity.ok(response);
    }
<<<<<<< HEAD
=======

    @PostMapping()
    public ResponseEntity<?> createExercise(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @RequestBody ExerciseRequest exerciseRequest) throws CustomException {
        var result = exerciseService.createExercise(userDetails.getId(), exerciseRequest);

        ApiResponse<?> response = ApiResponse.builder()
                .code(StatusCode.CREATED.getCode())
                .message(StatusCode.CREATED.getMessage())
                .data(result)
                .build();
        return ResponseEntity.status(StatusCode.CREATED.getCode()).body(response);
    }
>>>>>>> ee16b8f (imp api for exercise service)
}