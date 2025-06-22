package com.vn.EduQuest.services;


import com.vn.EduQuest.exceptions.CustomException;
import com.vn.EduQuest.payload.request.auth.ForgotPasswordRequest;
import com.vn.EduQuest.payload.request.auth.LoginRequest;
import com.vn.EduQuest.payload.request.auth.RefreshTokenRequest;
import com.vn.EduQuest.payload.request.auth.RegisterRequest;
import com.vn.EduQuest.payload.request.auth.ResetPasswordRequest;
import com.vn.EduQuest.payload.request.student.StudentDetailRequest;
import com.vn.EduQuest.payload.request.student.VerifyOtpRequest;
import com.vn.EduQuest.payload.response.auth.RegisterRespone;
import com.vn.EduQuest.payload.response.auth.TokenResponse;
import com.vn.EduQuest.payload.response.student.StudentDetailResponse;


public interface AuthService {    
    boolean initiatePasswordReset(ForgotPasswordRequest request) throws CustomException;
    boolean verifyOtpForgotPassword(VerifyOtpRequest request) throws CustomException;
    boolean resetPassword(ResetPasswordRequest request) throws CustomException;
    boolean logout(String token) throws CustomException;
    
    TokenResponse login(LoginRequest request) throws CustomException;
    TokenResponse refreshToken(RefreshTokenRequest refreshToken) throws CustomException;
    
    RegisterRespone register(RegisterRequest request) throws CustomException;
    StudentDetailResponse updateStudentDetails(Long userId, StudentDetailRequest request) throws CustomException;
    
    boolean verifyOTP(VerifyOtpRequest request) throws CustomException;
    boolean sendOTP(String username) throws CustomException;
    
}

