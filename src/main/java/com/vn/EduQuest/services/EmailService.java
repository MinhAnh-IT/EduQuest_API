package com.vn.EduQuest.services;

public interface EmailService {
    void sendOTPEmail(String to, String otp);
    void sendOtpEmail(String to, String username, String otp);
} 