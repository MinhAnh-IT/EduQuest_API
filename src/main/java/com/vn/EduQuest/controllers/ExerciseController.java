    // src/main/java/com/vn/EduQuest/controllers/ExerciseController.java
    package com.vn.EduQuest.controllers;

    import com.vn.EduQuest.enums.StatusCode;
    import com.vn.EduQuest.payload.ApiResponse;
    import com.vn.EduQuest.payload.response.Exercise.ExerciseResponse;
    import com.vn.EduQuest.security.UserDetailsImpl;
    import com.vn.EduQuest.services.ExerciseService;
    import lombok.RequiredArgsConstructor;
    import org.springframework.http.ResponseEntity;
    import org.springframework.security.core.annotation.AuthenticationPrincipal;
    import org.springframework.web.bind.annotation.*;

    import java.util.List;

    import com.vn.EduQuest.exceptions.CustomException;

    @RestController
    @RequestMapping("/api/exam")
    @RequiredArgsConstructor
    public class ExerciseController {
        private final ExerciseService exerciseService;

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
    }