package com.vn.EduQuest.utills;

import java.util.Random;
import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.RedisConnectionFailureException;
import org.springframework.stereotype.Service;

@Service
public class OTPService {

    @Autowired
    private RedisService redisService;

    private static final String OTP_PREFIX = "otp:";
    private static final long OTP_EXPIRY_TIME = 5; // Minutes

    public String generateOtp(String username) {
        try {
            String otp = String.format("%06d", new Random().nextInt(999999));
            String redisKey = OTP_PREFIX + username;
            redisService.set(redisKey, otp, OTP_EXPIRY_TIME, TimeUnit.MINUTES);
            return otp;
        } catch (RedisConnectionFailureException e) {
            throw new RuntimeException("Unable to connect to Redis", e);
        }
    }

    public boolean validateOtp(String username, String otp) {
        try {
            String redisKey = OTP_PREFIX + username;
            String storedOtp = (String) redisService.get(redisKey);
            return storedOtp != null && storedOtp.equals(otp);
        } catch (RedisConnectionFailureException e) {
            throw new RuntimeException("Unable to connect to Redis", e);
        }
    }

    public void clearOtp(String username) {
        try {
            String redisKey = OTP_PREFIX + username;
            redisService.delete(redisKey);
        } catch (RedisConnectionFailureException e) {
            throw new RuntimeException("Unable to connect to Redis", e);
        }
    }
}