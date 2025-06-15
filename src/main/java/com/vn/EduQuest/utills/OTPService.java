package com.vn.EduQuest.utills;

import java.util.Random;
import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.RedisConnectionFailureException;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

@Service
public class OTPService {

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    private static final long OTP_EXPIRY_TIME = 5; // Minutes

    public String generateOtp(String email) {
        try {
            String otp = String.format("%06d", new Random().nextInt(999999));
            redisTemplate.opsForValue().set(email, otp, OTP_EXPIRY_TIME, TimeUnit.MINUTES);
            return otp;
        } catch (RedisConnectionFailureException e) {
            throw new RuntimeException("Unable to connect to Redis", e);
        }
    }

    public boolean validateOtp(String email, String otp) {
        try {
            String storedOtp = (String) redisTemplate.opsForValue().get(email);
            return storedOtp != null && storedOtp.equals(otp);
        } catch (RedisConnectionFailureException e) {
            throw new RuntimeException("Unable to connect to Redis", e);
        }
    }

    public void clearOtp(String email) {
        try {
            redisTemplate.delete(email);
        } catch (RedisConnectionFailureException e) {
            throw new RuntimeException("Unable to connect to Redis", e);
        }
    }
}