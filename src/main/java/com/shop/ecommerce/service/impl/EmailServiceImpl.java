package com.shop.ecommerce.service.impl;

import com.shop.ecommerce.service.EmailService;
import com.shop.ecommerce.utils.MessageMultiUtils;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.MailException;
import org.springframework.mail.MailSendException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EmailServiceImpl implements EmailService {

    private final JavaMailSender javaMailSender;
    private final MessageMultiUtils messageMultiUtils;

    public void sendVerificationOtpEmail(String userEmail, String otp, String subject, String text) {
        try {
            MimeMessage mimeMessage = javaMailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, "UTF-8");
            helper.setSubject(subject);
            helper.setText(text);
            helper.setTo(userEmail);
            javaMailSender.send(mimeMessage);
        }catch (MailException | MessagingException e) {
            throw new MailSendException(messageMultiUtils.getMessage("email.send.error"));
        }
    }
}
