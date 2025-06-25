package com.vn.EduQuest.utills;

import java.util.Locale;
import java.util.concurrent.CompletableFuture;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.MailAuthenticationException;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;


@Service
@RequiredArgsConstructor
@Slf4j

public class EmailServiceImpl implements EmailService {
    private final JavaMailSender mailSender;
    @Autowired
    private SpringTemplateEngine springTemplate;

    @Override
    public void sendOTPEmail(String to, String otp, boolean isResend) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            
            helper.setTo(to);
            helper.setSubject("EduQuest - Xác Thực Tài Khoản");
            Context context = new Context(Locale.forLanguageTag("vi"));
            context.setVariable("otp", otp);
            context.setVariable("isResend", isResend);

            // Process template verify-otp.html
            String htmlContent = springTemplate.process("verify-otp", context);
            helper.setText(htmlContent, true);

            mailSender.send(message);
        } catch (MessagingException e) {
            throw new RuntimeException("Failed to send OTP email", e);
        }
    }

    @Override
      public void sendOtpEmail(String to, String username, String otp) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            helper.setTo(to);
            helper.setSubject("EduQuest - Password Reset OTP");

            // Prepare the evaluation context
            Context context = new Context(Locale.getDefault());
            context.setVariable("username", username);
            context.setVariable("otp", otp);

            // Create the HTML body using Thymeleaf
            String htmlContent = springTemplate.process("reset-password-email", context);
            helper.setText(htmlContent, true);

            mailSender.send(message);
              } catch (MailAuthenticationException e) {
            throw new RuntimeException("Email service authentication failed. Please check the email configuration.", e);
        } catch (MessagingException | MailException e) {
            throw new RuntimeException("Failed to send email: " + e.getMessage(), e);
        }
    }

    @Override
    @Async("emailTaskExecutor")
    public CompletableFuture<Void> sendOtpEmailAsync(String to, String username, String otp) {
        try {

            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            helper.setTo(to);
            helper.setSubject("EduQuest - Password Reset OTP");

            // Prepare the evaluation context
            Context context = new Context(Locale.getDefault());
            context.setVariable("username", username);
            context.setVariable("otp", otp);

            // Create the HTML body using Thymeleaf
            String htmlContent = springTemplate.process("reset-password-email", context);
            helper.setText(htmlContent, true);

            mailSender.send(message);

            return CompletableFuture.completedFuture(null);
            
        } catch (MailAuthenticationException e) {
            throw new RuntimeException("Email service authentication failed. Please check the email configuration.", e);
        } catch (MessagingException | MailException e) {
            throw new RuntimeException("Failed to send email: " + e.getMessage(), e);
        }
    }
} 