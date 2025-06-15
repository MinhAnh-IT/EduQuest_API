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
import com.vn.EduQuest.payload.request.JoinClassRequest;
import com.vn.EduQuest.payload.response.ClassValidationResponse;
import com.vn.EduQuest.payload.response.EnrollmentResponse;
import com.vn.EduQuest.services.EnrollmentService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/enrollments")
@RequiredArgsConstructor
public class EnrollmentController {

    private final EnrollmentService enrollmentService;

    /**
     * Join class method - requires user to be logged in
     * The flow is: Input class code → Check code → Check if already enrolled → If valid and not enrolled → Add to class
     * User must be authenticated to join a class
     */    @PostMapping("/join")
    public ResponseEntity<?> joinClass(
            @RequestHeader(value = "Authorization", required = false) String authHeader,
            @Valid @RequestBody JoinClassRequest joinClassRequest) throws CustomException {
        var result = enrollmentService.joinClass(authHeader, joinClassRequest);
        ApiResponse<?> response = ApiResponse.<EnrollmentResponse>builder()
                .code(StatusCode.OK.getCode())
                .message(StatusCode.OK.getMessage())
                .data(result)
                .build();
        return ResponseEntity.ok(response);
    }

    @PostMapping("/validate")
    public ResponseEntity<?> validateClassCode(
            @RequestHeader(value = "Authorization", required = false) String authHeader,
            @Valid @RequestBody JoinClassRequest joinClassRequest) throws CustomException {
        var result = enrollmentService.validateClassCode(joinClassRequest.getClassCode());
        ApiResponse<?> response = ApiResponse.<ClassValidationResponse>builder()
                .code(StatusCode.OK.getCode())
                .message(StatusCode.OK.getMessage())
                .data(result)
                .build();
        return ResponseEntity.ok(response);
    }    
    
    @DeleteMapping("/leave/{classId}")
    public ResponseEntity<?> leaveClass(
            @RequestHeader(value = "Authorization", required = false) String authHeader,
            @PathVariable Long classId) throws CustomException {
        var result = enrollmentService.leaveClass(authHeader, classId);
        ApiResponse<?> response = ApiResponse.<Boolean>builder()
                .code(StatusCode.OK.getCode())
                .message(StatusCode.OK.getMessage())
                .data(result)
                .build();
        return ResponseEntity.ok(response);
    }
}
