package com.shop.ecommerce.service;

public interface EmailService {
    void sendVerificationOtpEmail(String userEmail, String otp, String subject, String text);
}
