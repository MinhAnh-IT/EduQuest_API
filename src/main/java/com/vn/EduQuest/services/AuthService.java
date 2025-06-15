package com.vn.EduQuest.services;

import com.vn.EduQuest.exceptions.CustomException;
import com.vn.EduQuest.payload.request.ForgotPasswordRequest;
import com.vn.EduQuest.payload.request.LoginRequest;
import com.vn.EduQuest.payload.request.RefreshTokenRequest;
import com.vn.EduQuest.payload.request.RegisterRequest;
import com.vn.EduQuest.payload.request.ResetPasswordRequest;
import com.vn.EduQuest.payload.request.StudentDetailRequest;
import com.vn.EduQuest.payload.request.VerifyOtpRequest;
import com.vn.EduQuest.payload.response.RegisterRespone;
import com.vn.EduQuest.payload.response.StudentDetailResponse;
import com.vn.EduQuest.payload.response.TokenResponse;

public interface AuthService {    
    boolean initiatePasswordReset(ForgotPasswordRequest request) throws CustomException;
    boolean verifyOtpForgotPassword(VerifyOtpRequest request) throws CustomException; // Added method
    boolean resetPassword(ResetPasswordRequest request) throws CustomException;
    boolean logout(String token) throws CustomException;
    
    TokenResponse login(LoginRequest request) throws CustomException;
    TokenResponse refreshToken(RefreshTokenRequest refreshToken) throws CustomException;
    
    RegisterRespone register(RegisterRequest request) throws CustomException;
    StudentDetailResponse updateStudentDetails(Long userId, StudentDetailRequest request) throws CustomException;
    
    boolean verifyOTP(VerifyOtpRequest request) throws CustomException;
    boolean sendOTP(String username) throws CustomException;
}
