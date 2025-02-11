package com.shop.ecommerce.service;

import com.shop.ecommerce.modal.VerificationCode;

public interface VerificationCodeService {
    VerificationCode createVerificationCode(String otp, String email);
}
