package com.vn.EduQuest.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.vn.EduQuest.enums.StatusCode;
import com.vn.EduQuest.exceptions.CustomException;
import com.vn.EduQuest.payload.ApiResponse;
import com.vn.EduQuest.payload.request.student.JoinClassRequest;
import com.vn.EduQuest.services.EnrollmentService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/enrollments")
@RequiredArgsConstructor
public class EnrollmentController {

    private final EnrollmentService enrollmentService;

    @PostMapping("/join")
    public ResponseEntity<?> joinClass(
            @RequestHeader(value = "Authorization", required = false) String authHeader,
            @Valid @RequestBody JoinClassRequest joinClassRequest) throws CustomException {
        boolean result = enrollmentService.joinClass(authHeader, joinClassRequest);
        ApiResponse<?> response = ApiResponse.<Boolean>builder()
                .code(StatusCode.OK.getCode())
                .message("Successfully joined the class. Your enrollment is pending approval.")
                .data(result)
                .build();
        return ResponseEntity.ok(response);
    }

    @PostMapping("/validate")
    public ResponseEntity<?> validateClassCode(
            @RequestHeader(value = "Authorization", required = false) String authHeader,
            @Valid @RequestBody JoinClassRequest joinClassRequest) throws CustomException {
        boolean result = enrollmentService.validateClassCode(joinClassRequest.getClassCode());
        ApiResponse<?> response = ApiResponse.<Boolean>builder()
                .code(StatusCode.OK.getCode())
                .message("Class code is valid")
                .data(result)
                .build();
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/leave/{classId}")
    public ResponseEntity<?> leaveClass(
            @RequestHeader(value = "Authorization", required = false) String authHeader,
            @PathVariable Long classId) throws CustomException {
        boolean result = enrollmentService.leaveClass(authHeader, classId);
        ApiResponse<?> response = ApiResponse.<Boolean>builder()
                .code(StatusCode.OK.getCode())
                .message("Successfully left the class")
                .data(result)
                .build();
        return ResponseEntity.ok(response);
    }
}
