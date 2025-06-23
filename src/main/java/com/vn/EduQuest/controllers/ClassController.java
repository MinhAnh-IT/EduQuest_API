package com.vn.EduQuest.controllers;

import java.util.List;

import com.vn.EduQuest.payload.request.Class.ClassCreateRequest;
import com.vn.EduQuest.payload.response.clazz.ClassCreateResponse;
import com.vn.EduQuest.payload.response.clazz.InstructorClassResponse;
import com.vn.EduQuest.security.UserDetailsImpl;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import com.vn.EduQuest.enums.StatusCode;
import com.vn.EduQuest.exceptions.CustomException;
import com.vn.EduQuest.payload.ApiResponse;
import com.vn.EduQuest.payload.response.clazz.ClassDetailResponse;
import com.vn.EduQuest.payload.response.student.StudentInClassResponse;
import com.vn.EduQuest.services.ClassService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/classes")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ClassController {

    private final ClassService classService;

    @GetMapping("/{classId}/detail")
    public ResponseEntity<?> getClassDetail(@PathVariable Long classId) throws CustomException {
        ClassDetailResponse result = classService.getClassDetail(classId);
        ApiResponse<?> response = ApiResponse.<ClassDetailResponse>builder()
                .code(StatusCode.OK.getCode())
                .message("Successfully retrieved class detail")
                .data(result)
                .build();
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{classId}/students")
    public ResponseEntity<?> getStudentsInClass(@PathVariable Long classId) throws CustomException {
        List<StudentInClassResponse> result = classService.getStudentsInClass(classId);
        String message = result.isEmpty()
                ? "No students found in this class"
                : "Successfully retrieved students in class";
        ApiResponse<?> response = ApiResponse.<List<StudentInClassResponse>>builder()
                .code(StatusCode.OK.getCode())
                .message(message)
                .data(result)
                .build();
        return ResponseEntity.ok(response);
    }

    @PostMapping("/create")
    public ResponseEntity<?> createClass(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @Valid @RequestBody ClassCreateRequest request) throws CustomException {
        ClassCreateResponse result = classService.createClass(userDetails.getId(), request);
        ApiResponse<?> response = ApiResponse.<ClassCreateResponse>builder()
                .code(StatusCode.OK.getCode())
                .message(StatusCode.CREATED.getMessage())
                .data(result)
                .build();
        return ResponseEntity.ok(response);
    }
    @GetMapping("/instructors")
    public ResponseEntity<?> getInstructorClasses(@AuthenticationPrincipal UserDetailsImpl userDetails) throws CustomException {
        List<InstructorClassResponse> result = classService.getInstructorClasses(userDetails.getId());

        ApiResponse<?> response = ApiResponse.builder()
                .code(StatusCode.OK.getCode())
                .message(StatusCode.OK.getMessage())
                .data(result)
                .build();
        return ResponseEntity.ok(response);
    }

}

