package com.vn.EduQuest.services;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import lombok.RequiredArgsConstructor;
import java.util.Random;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class OTPServiceImpl implements OTPService {
    private final RedisTemplate<String, Object> redisTemplate;
    // private static final long OTP_EXPIRY_TIME = 5; // Minutes
    @Value("${otp.expiration}")
    private long otpExpirationSeconds;

    @Override
    public String generateOTP(String key) {
        String otp = String.format("%06d", new Random().nextInt(999999));
        redisTemplate.opsForValue().set(key, otp, otpExpirationSeconds, TimeUnit.SECONDS);
        return otp;
    }

    @Override
    public boolean validateOTP(String key, String otp) {
        Object storedOTP = redisTemplate.opsForValue().get(key);
        return storedOTP != null && storedOTP.toString().equals(otp);
    }

    @Override
    public void clearOTP(String key) {
        redisTemplate.delete(key);
    }
}