package com.vn.EduQuest.services;

import com.vn.EduQuest.exceptions.CustomException;
import com.vn.EduQuest.payload.request.ForgotPasswordRequest;
import com.vn.EduQuest.payload.request.ResetPasswordRequest;
import com.vn.EduQuest.payload.response.LoginResponse;

public interface AuthService {
    LoginResponse login(String username, String password) throws CustomException;
    
    // Forgot password methods
    void initiatePasswordReset(ForgotPasswordRequest request) throws CustomException;
    void resetPassword(ResetPasswordRequest request) throws CustomException;
}
