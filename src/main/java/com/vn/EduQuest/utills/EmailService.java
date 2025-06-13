package com.vn.EduQuest.utills;

public interface EmailService {
    void sendOTPEmail(String to, String otp);
    void sendOtpEmail(String to, String username, String otp);
} 