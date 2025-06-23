package com.vn.EduQuest.controllers;

import java.util.List;

import com.vn.EduQuest.payload.request.Class.EnrollmentApprovalRequest;
import com.vn.EduQuest.payload.response.clazz.EnrollmentResponsee;
import com.vn.EduQuest.payload.response.enrollment.PendingEnrollmentResponse;
import com.vn.EduQuest.security.UserDetailsImpl;
import com.vn.EduQuest.services.ClassService;
import lombok.experimental.FieldDefaults;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import com.vn.EduQuest.enums.StatusCode;
import com.vn.EduQuest.exceptions.CustomException;
import com.vn.EduQuest.payload.ApiResponse;
import com.vn.EduQuest.payload.request.student.JoinClassRequest;
import com.vn.EduQuest.payload.response.enrollment.EnrollmentResponse;
import com.vn.EduQuest.services.EnrollmentService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/enrollments")
@RequiredArgsConstructor
@FieldDefaults(level = lombok.AccessLevel.PRIVATE, makeFinal = true)
public class EnrollmentController {

    EnrollmentService enrollmentService;

    @PostMapping("/join")
    public ResponseEntity<?> joinClass(@Valid @RequestBody JoinClassRequest joinClassRequest) throws CustomException {
        boolean result = enrollmentService.joinClass(null, joinClassRequest);
        ApiResponse<?> response = ApiResponse.<Boolean>builder()
                .code(StatusCode.OK.getCode())
                .message("Successfully joined the class. Your enrollment is pending approval.")
                .data(result)
                .build();
        return ResponseEntity.ok(response);
    }    
    
    @PostMapping("/validate")
    public ResponseEntity<?> validateClassCode(@Valid @RequestBody JoinClassRequest joinClassRequest) throws CustomException {
        boolean result = enrollmentService.validateClassCode(joinClassRequest.getClassCode());
        ApiResponse<?> response = ApiResponse.<Boolean>builder()
                .code(StatusCode.OK.getCode())
                .message("Class code is valid")
                .data(result)
                .build();        
        return ResponseEntity.ok(response);
    }

    @GetMapping("/my-classes")
    public ResponseEntity<?> getMyEnrollments() throws CustomException {
        List<EnrollmentResponse> result = enrollmentService.getStudentEnrollments(null);
        String message = result.isEmpty() 
            ? "No enrollments found" 
            : "Successfully retrieved student enrollments";
        ApiResponse<?> response = ApiResponse.<List<EnrollmentResponse>>builder()
                .code(StatusCode.OK.getCode())
                .message(message)
                .data(result)
                .build();
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/my-enrolled-classes")
    public ResponseEntity<?> getMyEnrolledClasses() throws CustomException {
        List<EnrollmentResponse> result = enrollmentService.getStudentEnrolledClasses(null);
        String message = result.isEmpty() 
            ? "No enrolled classes found" 
            : "Successfully retrieved student enrolled classes";
        ApiResponse<?> response = ApiResponse.<List<EnrollmentResponse>>builder()
                .code(StatusCode.OK.getCode())
                .message(message)
                .data(result)
                .build();
        return ResponseEntity.ok(response);
    }
    
    @DeleteMapping("/leave/{classId}")
    public ResponseEntity<?> leaveClass(@PathVariable Long classId) throws CustomException {
        boolean result = enrollmentService.leaveClass(null, classId);
        ApiResponse<?> response = ApiResponse.<Boolean>builder()
                .code(StatusCode.OK.getCode())
                .message("Successfully left the class")
                .data(result)
                .build();
        return ResponseEntity.ok(response);
    }

    @PutMapping("/approve")
    public ResponseEntity<?> approveEnrollment(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @Valid @RequestBody EnrollmentApprovalRequest approvalRequest) throws CustomException {
        EnrollmentResponsee result = enrollmentService.enrollStudent(userDetails.getId(), approvalRequest);
        ApiResponse<?> response = ApiResponse.builder()
                .code(StatusCode.OK.getCode())
                .message(StatusCode.OK.getMessage())
                .data(result)
                .build();
        return ResponseEntity.ok(response);
    }
    @GetMapping("/{classId}/pending-enrollments")
    public ResponseEntity<?> getPendingEnrollments(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @PathVariable Long classId) throws CustomException {
        List<PendingEnrollmentResponse> result = enrollmentService.getPendingEnrollments(userDetails.getId(), classId);

        ApiResponse<?> response = ApiResponse.builder()
                .code(StatusCode.OK.getCode())
                .message( StatusCode.OK.getMessage())
                .data(result)
                .build();
        return ResponseEntity.ok(response);
    }
}