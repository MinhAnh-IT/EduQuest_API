package com.vn.EduQuest.services;

import com.vn.EduQuest.exceptions.CustomException;
import com.vn.EduQuest.payload.request.RegisterRequest;
import com.vn.EduQuest.payload.request.StudentDetailRequest;
import com.vn.EduQuest.payload.request.VerifyOtpRequest;
import com.vn.EduQuest.payload.response.LoginResponse;
import com.vn.EduQuest.payload.response.RegisterRespone;

public interface AuthService {
    LoginResponse login(String username, String password);
    RegisterRespone register(RegisterRequest request) throws CustomException;
    RegisterRespone updateStudentDetails(Long userId, StudentDetailRequest request) throws CustomException;
    RegisterRespone verifyOTP(VerifyOtpRequest request) throws CustomException;
    void resendOTP(String email) throws CustomException;
}
