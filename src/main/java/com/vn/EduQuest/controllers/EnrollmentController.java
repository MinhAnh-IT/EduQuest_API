package com.vn.EduQuest.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.vn.EduQuest.entities.User;
import com.vn.EduQuest.enums.StatusCode;
import com.vn.EduQuest.exceptions.CustomException;
import com.vn.EduQuest.payload.ApiResponse;
import com.vn.EduQuest.payload.request.JoinClassRequest;
import com.vn.EduQuest.payload.response.ClassValidationResponse;
import com.vn.EduQuest.payload.response.EnrollmentResponse;
import com.vn.EduQuest.repositories.UserRepository;
import com.vn.EduQuest.services.EnrollmentService;
import com.vn.EduQuest.utills.JwtService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/enrollments")
@RequiredArgsConstructor
public class EnrollmentController {

    private final EnrollmentService enrollmentService;
    private final UserRepository userRepository;
    private final JwtService jwtService;/**
     * Join class method - requires user to be logged in
     * The flow is: Input class code → Check code → Check if already enrolled → If valid and not enrolled → Add to class
     * User must be authenticated to join a class     */
    @PostMapping("/join")
    public ResponseEntity<?> joinClass(
            @RequestHeader(value = "Authorization", required = false) String authHeader,
            @Valid @RequestBody JoinClassRequest joinClassRequest) throws CustomException {
        
        // Validate that class code is provided
        if (joinClassRequest.getClassCode() == null || joinClassRequest.getClassCode().trim().isEmpty()) {
            throw new CustomException(StatusCode.BAD_REQUEST, "Class code is required");
        }
        
        // Extract and validate token from Authorization header
        User currentUser = null;
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);
            if (jwtService.validateToken(token)) {
                Long userId = jwtService.getUserIdFromJWT(token);
                currentUser = userRepository.findById(userId).orElse(null);
            }
        }
        
        // Require authentication for joining classes
        if (currentUser == null) {
            throw new CustomException(StatusCode.BAD_REQUEST, "You must be logged in to join a class");
        }
        
        // Try to join the class
        try {
            EnrollmentResponse enrollmentData = enrollmentService.joinClass(currentUser, joinClassRequest);
            
            ApiResponse<EnrollmentResponse> response = ApiResponse.<EnrollmentResponse>builder()
                    .code(StatusCode.OK.getCode())
                    .message("Successfully joined class")
                    .data(enrollmentData)
                    .build();
            return ResponseEntity.ok(response);
        } catch (CustomException e) {
            // If the exception is that student is already enrolled, we'll return a more user-friendly message
            if (e.getErrorCode() == StatusCode.STUDENT_ALREADY_ENROLLED_IN_CLASS) {
                // Get class information for the response
                ClassValidationResponse validationData = enrollmentService.validateClassCode(joinClassRequest.getClassCode());
                
                ApiResponse<ClassValidationResponse> response = ApiResponse.<ClassValidationResponse>builder()
                        .code(e.getErrorCode().getCode())
                        .message("You are already enrolled in this class")
                        .data(validationData)
                        .build();
                return ResponseEntity.ok(response);
            } else {
                // Re-throw other exceptions
                throw e;
            }
        }
    }
      /**
     * Validate a class code without joining
     * This endpoint can be used without authentication to check if a class code is valid
     */
    @PostMapping("/validate")
    public ResponseEntity<?> validateClassCode(
            @RequestHeader(value = "Authorization", required = false) String authHeader,
            @Valid @RequestBody JoinClassRequest joinClassRequest) throws CustomException {
        
        // Validate that class code is provided
        if (joinClassRequest.getClassCode() == null || joinClassRequest.getClassCode().trim().isEmpty()) {
            throw new CustomException(StatusCode.BAD_REQUEST, "Class code is required");
        }
        
        // Check if user is authenticated (optional for validation)
        User currentUser = null;
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);
            if (jwtService.validateToken(token)) {
                Long userId = jwtService.getUserIdFromJWT(token);
                currentUser = userRepository.findById(userId).orElse(null);
            }
        }
        
        // Validate the class code - if user is authenticated, we'll check if they can join
        ClassValidationResponse validationData;
        String message;
        
        if (currentUser != null) {
            try {
                // Check if this user is already enrolled in this class
                validationData = enrollmentService.validateClassCode(joinClassRequest.getClassCode());
                message = "Class code is valid. You can join this class.";
            } catch (CustomException e) {
                if (e.getErrorCode() == StatusCode.STUDENT_ALREADY_ENROLLED_IN_CLASS) {
                    validationData = enrollmentService.validateClassCode(joinClassRequest.getClassCode());
                    message = "You are already enrolled in this class.";
                } else {
                    throw e;
                }
            }
        } else {
            validationData = enrollmentService.validateClassCode(joinClassRequest.getClassCode());
            message = "Class code validated successfully. Please login to join.";
        }
        
        ApiResponse<ClassValidationResponse> response = ApiResponse.<ClassValidationResponse>builder()
                .code(StatusCode.OK.getCode())
                .message(message)
                .data(validationData)
                .build();
        return ResponseEntity.ok(response);
    }    @DeleteMapping("/leave/{classId}")
    public ResponseEntity<ApiResponse<Void>> leaveClass(
            @RequestHeader(value = "Authorization", required = false) String authHeader,
            @PathVariable Long classId) throws CustomException {
        
        // Extract and validate token from Authorization header
        User currentUser = null;
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);
            if (jwtService.validateToken(token)) {
                Long userId = jwtService.getUserIdFromJWT(token);
                currentUser = userRepository.findById(userId).orElse(null);
            }
        }
        
        // Require authentication for leaving classes
        if (currentUser == null) {
            throw new CustomException(StatusCode.BAD_REQUEST, "You must be logged in to leave a class");
        }
            
        enrollmentService.leaveClass(currentUser, classId);
        ApiResponse<Void> response = ApiResponse.<Void>builder()
                .code(StatusCode.OK.getCode())
                .message("Successfully left class")
                .build();
        return ResponseEntity.ok(response);
    }
}
