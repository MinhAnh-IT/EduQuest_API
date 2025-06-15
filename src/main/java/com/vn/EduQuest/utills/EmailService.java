package com.vn.EduQuest.utills;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.MailAuthenticationException;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;

    public void sendOtpEmail(String to, String otp) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(to);
            message.setSubject("EduQuest - Password Reset OTP");
            message.setText("""
                    Dear User,

                    You have requested to reset your password. Here is your OTP:

                    %s

                    This OTP is valid for 5 minutes.

                    If you did not request this, please ignore this email.

                    Best regards,
                    EduQuest Team
                    """.formatted(otp));
            mailSender.send(message);
        } catch (MailAuthenticationException e) {
            throw new RuntimeException("Email service authentication failed. Please check the email configuration.", e);
        } catch (MailException e) {
            throw new RuntimeException("Failed to send email: " + e.getMessage(), e);
        }
    }
}