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
import com.vn.EduQuest.payload.request.LogoutRequest;
import com.vn.EduQuest.payload.request.ResetPasswordRequest;
import com.vn.EduQuest.payload.request.VerifyOtpRequest;
import com.vn.EduQuest.services.AuthService;

import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

@RestController
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    @PostMapping("/forgot-password")
    public ResponseEntity<ApiResponse<String>> forgotPassword(
            @Valid @RequestBody ForgotPasswordRequest request) throws CustomException {
        if (authService.initiatePasswordReset(request)) {
            return ResponseEntity.ok(ApiResponse.<String>builder()
                    .code(StatusCode.OK.getCode())
                    .message("OTP has been sent to your email")
                    .build());
        }
        // Fallback for unexpected false return without exception (should not happen based on design)
        return ResponseEntity.status(StatusCode.INTERNAL_SERVER_ERROR.getCode())
                .body(ApiResponse.<String>builder()
                        .code(StatusCode.INTERNAL_SERVER_ERROR.getCode())
                        .message("Failed to initiate password reset.")
                        .build());
    }

    @PostMapping("/verify-otp")
    public ResponseEntity<ApiResponse<String>> verifyOtp(
            @Valid @RequestBody VerifyOtpRequest request) throws CustomException {
        if (authService.verifyOtp(request)) {
            return ResponseEntity.ok(ApiResponse.<String>builder()
                    .code(StatusCode.OTP_VERIFIED_SUCCESS.getCode())
                    .message(StatusCode.OTP_VERIFIED_SUCCESS.getMessage())
                    .build());
        }
        // Fallback for unexpected false return without exception
        return ResponseEntity.status(StatusCode.INTERNAL_SERVER_ERROR.getCode())
                .body(ApiResponse.<String>builder()
                        .code(StatusCode.INTERNAL_SERVER_ERROR.getCode())
                        .message("Failed to verify OTP.")
                        .build());
    }

    @PostMapping("/reset-password")
    public ResponseEntity<ApiResponse<String>> resetPassword(
            @Valid @RequestBody ResetPasswordRequest request) throws CustomException {
        if (authService.resetPassword(request)) {
            return ResponseEntity.ok(ApiResponse.<String>builder()
                    .code(StatusCode.PASSWORD_RESET_SUCCESS.getCode())
                    .message(StatusCode.PASSWORD_RESET_SUCCESS.getMessage())
                    .build());
        }
        // Fallback for unexpected false return without exception
        return ResponseEntity.status(StatusCode.INTERNAL_SERVER_ERROR.getCode())
                .body(ApiResponse.<String>builder()
                        .code(StatusCode.INTERNAL_SERVER_ERROR.getCode())
                        .message("Failed to reset password.")
                        .build());
    }

    @PostMapping("/logout")
    public ResponseEntity<ApiResponse<String>> logout(@Valid @RequestBody LogoutRequest request) throws CustomException {
        if (authService.logout(request)) {
            return ResponseEntity.ok(ApiResponse.<String>builder()
                    .code(StatusCode.LOGOUT_SUCCESS.getCode())
                    .message(StatusCode.LOGOUT_SUCCESS.getMessage())
                    .build());
        }
        // Fallback for unexpected false return without exception
        return ResponseEntity.status(StatusCode.INTERNAL_SERVER_ERROR.getCode())
                .body(ApiResponse.<String>builder()
                        .code(StatusCode.INTERNAL_SERVER_ERROR.getCode())
                        .message("Logout failed.")
                        .build());
    }
}