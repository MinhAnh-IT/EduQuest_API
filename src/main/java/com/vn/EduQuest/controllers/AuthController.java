package com.vn.EduQuest.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.vn.EduQuest.enums.StatusCode;
import com.vn.EduQuest.exceptions.CustomException;
import com.vn.EduQuest.payload.ApiResponse;
import com.vn.EduQuest.payload.request.ForgotPasswordRequest;
import com.vn.EduQuest.payload.request.ResetPasswordRequest;
import com.vn.EduQuest.services.AuthService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/forgot-password")
    public ResponseEntity<ApiResponse<String>> forgotPassword(
            @Valid @RequestBody ForgotPasswordRequest request) {
        try {
            authService.initiatePasswordReset(request);
            return ResponseEntity.ok(ApiResponse.<String>builder()
                    .code(StatusCode.OK.getCode())
                    .message("OTP has been sent to your registered email")
                    .build());
        } catch (CustomException e) {
            return ResponseEntity.status(e.getErrorCode().getCode())
                    .body(ApiResponse.<String>builder()
                            .code(e.getErrorCode().getCode())
                            .message(e.getMessage())
                            .build());
        }
    }

    @PostMapping("/reset-password")
    public ResponseEntity<ApiResponse<String>> resetPassword(
            @Valid @RequestBody ResetPasswordRequest request) {
        try {
            authService.resetPassword(request);
            return ResponseEntity.ok(ApiResponse.<String>builder()
                    .code(StatusCode.OK.getCode())
                    .message("Password has been reset successfully")
                    .build());
        } catch (CustomException e) {
            return ResponseEntity.status(e.getErrorCode().getCode())
                    .body(ApiResponse.<String>builder()
                            .code(e.getErrorCode().getCode())
                            .message(e.getMessage())
                            .build());
        }
    }
}