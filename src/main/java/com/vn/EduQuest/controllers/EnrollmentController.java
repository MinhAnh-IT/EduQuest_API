package com.vn.EduQuest.controllers;

import com.vn.EduQuest.entities.User;
import com.vn.EduQuest.payload.request.JoinClassRequest;
import com.vn.EduQuest.payload.ApiResponse;
import com.vn.EduQuest.payload.response.EnrollmentResponse;
import com.vn.EduQuest.services.EnrollmentService;
import com.vn.EduQuest.exceptions.CustomException;
import com.vn.EduQuest.enums.StatusCode;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/enrollments")
@RequiredArgsConstructor
public class EnrollmentController {

    private final EnrollmentService enrollmentService;

    @PostMapping("/join")
    public ResponseEntity<ApiResponse<EnrollmentResponse>> joinClass(
            @AuthenticationPrincipal User currentUser, // Get the authenticated user
            @Valid @RequestBody JoinClassRequest joinClassRequest) throws CustomException {
        EnrollmentResponse enrollmentData = enrollmentService.joinClass(currentUser, joinClassRequest);
        ApiResponse<EnrollmentResponse> response = ApiResponse.<EnrollmentResponse>builder()
                .code(StatusCode.OK.getCode())
                .message("Successfully joined class") // Specific success message
                .data(enrollmentData)
                .build();
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/leave/{classId}")
    public ResponseEntity<ApiResponse<Void>> leaveClass(
            @AuthenticationPrincipal User currentUser, // Get the authenticated user
            @PathVariable Long classId) throws CustomException {
        enrollmentService.leaveClass(currentUser, classId);
        ApiResponse<Void> response = ApiResponse.<Void>builder()
                .code(StatusCode.OK.getCode())
                .message("Successfully left class") // Specific success message
                .build();
        return ResponseEntity.ok(response);
    }
}
