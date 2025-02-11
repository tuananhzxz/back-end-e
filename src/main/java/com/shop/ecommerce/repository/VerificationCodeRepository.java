package com.shop.ecommerce.repository;

import com.shop.ecommerce.modal.VerificationCode;
import org.apache.juli.VerbatimFormatter;
import org.springframework.data.jpa.repository.JpaRepository;

public interface VerificationCodeRepository extends JpaRepository<VerificationCode, Long> {
    VerificationCode findByEmail(String email);
    VerificationCode findByOtp(String otp);
}
