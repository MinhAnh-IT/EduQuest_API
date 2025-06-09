package com.vn.EduQuest.services;

import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class EmailServiceImpl implements EmailService {
    private final JavaMailSender mailSender;

    @Override
    public void sendOTPEmail(String to, String otp) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject("EduQuest - Your OTP Code");
        message.setText("Your OTP code is: " + otp + "\nThis code will expire in 5 minutes.");
        mailSender.send(message);
    }
} 