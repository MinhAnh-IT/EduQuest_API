package com.vn.EduQuest.services;

public interface OTPService {
    String generateOTP(String key);
    boolean validateOTP(String key, String otp);
    void clearOTP(String key);
} 