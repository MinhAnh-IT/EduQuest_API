package com.vn.EduQuest.utills;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
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
    private static final String EXERCISE_EMAIL_TEMPLATE_PATH = "templates/exercise-notification-email-template.html";
    private static final String FROM_EMAIL = "no-reply@eduquest.vn";

    private final JavaMailSender mailSender;

    @Autowired
    private SpringTemplateEngine springTemplate;
    @Override
    @Async("emailTaskExecutor")
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
       } catch (Exception e) {
        log.error("Failed to send OTP email to {}: {}", to, e.getMessage());
    }
        sendOtpWithTemplate(to, "EduQuest - Xác Thực Tài Khoản", "verify-otp", Map.of(
                "otp", otp,
                "isResend", String.valueOf(isResend)
        ));
    }

    @Override
    public void sendOtpEmail(String to, String username, String otp) {
        sendOtpWithTemplate(to, "EduQuest - Password Reset OTP", "reset-password-email", Map.of(
                "username", username,
                "otp", otp
        ));
    }

    @Override
    @Async("emailTaskExecutor")
    public CompletableFuture<Void> sendOtpEmailAsync(String to, String username, String otp) {
        sendOtpWithTemplate(to, "EduQuest - Password Reset OTP", "reset-password-email", Map.of(
                "username", username,
                "otp", otp
        ));
        return CompletableFuture.completedFuture(null);
    }

    // ======== EXERCISE NOTIFICATION EMAIL ========
    @Override
    @Async("emailTaskExecutor")
    public CompletableFuture<Void> sendExerciseCreatedNotificationAsync(String to, HashMap<String, String> data) {
        return CompletableFuture.runAsync(() -> {
            try {
                String template = readTemplate(EXERCISE_EMAIL_TEMPLATE_PATH);
                String content = fillTemplate(template, data);
                sendHtmlEmail(to, "Thông báo bài tập mới trên EduQuest", content);
            } catch (Exception e) {
                log.error("Gửi mail thất bại: {}", e.getMessage(), e);
                throw new RuntimeException("Gửi mail thất bại: " + e.getMessage());
            }
        });
    }

    private void sendOtpWithTemplate(String to, String subject, String templateName, Map<String, String> vars) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            helper.setTo(to);
            helper.setSubject(subject);

            Context context = new Context(Locale.forLanguageTag("vi"));
            vars.forEach(context::setVariable);

            String htmlContent = springTemplate.process(templateName, context);
            helper.setText(htmlContent, true);
            helper.setFrom(FROM_EMAIL);

            mailSender.send(message);
        } catch (MailAuthenticationException e) {
            throw new RuntimeException("Email service authentication failed. Please check the email configuration.", e);
        } catch (MessagingException | MailException e) {
            throw new RuntimeException("Failed to send email: " + e.getMessage(), e);
        }
    }

    private String readTemplate(String path) throws IOException {
        ClassLoader classLoader = getClass().getClassLoader();
        try (InputStream is = classLoader.getResourceAsStream(path)) {
            if (is == null) throw new FileNotFoundException("Template not found: " + path);
            return new String(is.readAllBytes(), StandardCharsets.UTF_8);
        }
    }

    private String fillTemplate(String template, Map<String, String> data) {
        String result = template;
        for (Map.Entry<String, String> entry : data.entrySet()) {
            result = result.replace("{" + entry.getKey() + "}", entry.getValue());
        }
        return result;
    }

    private void sendHtmlEmail(String to, String subject, String htmlContent) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(htmlContent, true);
            helper.setFrom(FROM_EMAIL);
            mailSender.send(message);
        } catch (MessagingException e) {
            throw new RuntimeException("Failed to send HTML email", e);
        }
    }
}
