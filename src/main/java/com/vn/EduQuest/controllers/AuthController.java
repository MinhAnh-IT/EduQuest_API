package com.vn.EduQuest.controllers;

import com.vn.EduQuest.enums.StatusCode;
import com.vn.EduQuest.exceptions.CustomException;
import com.vn.EduQuest.payload.ApiResponse;
import com.vn.EduQuest.payload.request.RegisterRequest;
import com.vn.EduQuest.payload.request.StudentDetailRequest;
import com.vn.EduQuest.payload.request.VerifyOtpRequest;
import com.vn.EduQuest.payload.response.RegisterRespone;
import com.vn.EduQuest.services.AuthService;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import lombok.extern.slf4j.Slf4j;

@RequiredArgsConstructor
@Slf4j
@RestController
@RequestMapping("/api/auth")
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AuthController {
    AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<?> register(
            @Valid @RequestBody RegisterRequest request) throws CustomException {
        RegisterRespone response = authService.register(request);
        ApiResponse<?> apiResponse = ApiResponse.<RegisterRespone>builder()
                .code(StatusCode.CREATED.getCode())
                .message(StatusCode.CREATED.getMessage())
                .data(response)
                .build();
        return ResponseEntity.status(HttpStatus.CREATED).body(apiResponse);
    }

    @PostMapping("/students/{userId}/details")
    public ResponseEntity<?> updateStudentDetails(
            @PathVariable Long userId,
            @Valid @RequestBody StudentDetailRequest request) throws CustomException {
        RegisterRespone response = authService.updateStudentDetails(userId, request);
        ApiResponse<?> apiResponse = ApiResponse.<RegisterRespone>builder()
                .code(StatusCode.CREATED.getCode())
                .message(StatusCode.CREATED.getMessage())
                .data(response)
                .build();
        return ResponseEntity.status(HttpStatus.CREATED).body(apiResponse);
    }

    @PostMapping("/verify-otp")
    public ResponseEntity<?> verifyOTP(@Valid @RequestBody VerifyOtpRequest request) throws CustomException {
        RegisterRespone response = authService.verifyOTP(request);
        ApiResponse<?> apiResponse = ApiResponse.<RegisterRespone>builder()
                .code(StatusCode.OK.getCode())
                .message(StatusCode.OK.getMessage())
                .data(response)
                .build();
        return ResponseEntity.ok(apiResponse);
    }

    @PostMapping("/resend-otp")
    public ResponseEntity<?> resendOTP(@RequestParam String email) throws CustomException {
        authService.resendOTP(email);
        ApiResponse<?> apiResponse = ApiResponse.builder()
                .code(StatusCode.OTP_SENT.getCode())
                .message(StatusCode.OTP_SENT.getMessage())
                .data(true)
                .build();
        return ResponseEntity.ok(apiResponse);
    }
}