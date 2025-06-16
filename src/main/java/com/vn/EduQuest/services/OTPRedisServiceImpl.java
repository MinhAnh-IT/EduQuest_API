package com.vn.EduQuest.services;

import java.util.concurrent.TimeUnit;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class OTPRedisServiceImpl implements OTPRedisService {
    private final RedisTemplate<String, String> redisTemplate;
    
    @Override
    public void saveOTP(String key, String otp, long expirationTime) {
        try {
            redisTemplate.opsForValue().set(key, otp, expirationTime, TimeUnit.SECONDS);
            log.info("Saved OTP to Redis with key: {}", key);
        } catch (Exception e) {
            log.error("Error saving OTP to Redis: ", e);
            throw new RuntimeException("Failed to save OTP");
        }
    }

    @Override
    public String getOTP(String key) {
        try {
            return redisTemplate.opsForValue().get(key);
        } catch (Exception e) {
            log.error("Error getting OTP from Redis: ", e);
            return null;
        }
    }

    @Override
    public void deleteOTP(String key) {
        try {
            redisTemplate.delete(key);
            log.info("Deleted OTP from Redis with key: {}", key);
        } catch (Exception e) {
            log.error("Error deleting OTP from Redis: ", e);
        }
    }
}