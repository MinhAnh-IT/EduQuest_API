package com.vn.EduQuest.utills;

import java.util.Random;

import org.springframework.stereotype.Service;

@Service
public class OTPService {

    /**
     * Generates a 6-digit numeric OTP.
     * This method no longer interacts with Redis.
     * @param username The username for whom the OTP is generated (can be used for logging or context, but not for storage here).
     * @return A 6-digit OTP string.
     */
    public String generateOtp(String username) {
        // Username parameter is kept for potential logging or context, though not used in generation itself.
        return String.format("%06d", new Random().nextInt(999999));
    }

    /**
     * Validates the provided OTP against a stored OTP.
     * This method no longer interacts with Redis to fetch the stored OTP.
     * @param providedOtp The OTP provided by the user.
     * @param storedOtp The OTP retrieved from the cache (e.g., Redis) by the calling service.
     * @return True if the OTPs match and the storedOtp is not null, false otherwise.
     */
    public boolean validateOtp(String providedOtp, String storedOtp) {
        return storedOtp != null && storedOtp.equals(providedOtp);
    }
}