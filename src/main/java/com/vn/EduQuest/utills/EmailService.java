package com.vn.EduQuest.utills;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.MailAuthenticationException;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.spring6.SpringTemplateEngine;
import org.thymeleaf.context.Context;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import java.util.Locale;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;

    @Autowired
    private SpringTemplateEngine templateEngine;

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
            String htmlContent = templateEngine.process("reset-password-email", context);
            helper.setText(htmlContent, true);

            mailSender.send(message);
            
        } catch (MailAuthenticationException e) {
            throw new RuntimeException("Email service authentication failed. Please check the email configuration.", e);
        } catch (MessagingException | MailException e) {
            throw new RuntimeException("Failed to send email: " + e.getMessage(), e);
        }
    }
}