package com.vn.EduQuest.services;

public interface OTPRedisService {
    void saveOTP(String key, String otp, long expirationTime);
    String getOTP(String key);
    void deleteOTP(String key);
}
