package com.vn.EduQuest.utills;

import org.springframework.stereotype.Service;
import lombok.RequiredArgsConstructor;
import java.util.Random;

@Service
@RequiredArgsConstructor
public class OTPServiceImpl implements OTPService {
  @Override
    public String generateOTP( String username) {
        // Chỉ tạo OTP, không lưu vào Redis
        return String.format("%06d", new Random().nextInt(999999));
    }

    @Override
    public boolean validateOtp(String providedOtp, String storedOtp) {
        return storedOtp != null && storedOtp.equals(providedOtp);
    }
}