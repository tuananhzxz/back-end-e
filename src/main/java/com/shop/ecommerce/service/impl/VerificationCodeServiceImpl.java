package com.shop.ecommerce.service.impl;

import com.shop.ecommerce.modal.VerificationCode;
import com.shop.ecommerce.repository.VerificationCodeRepository;
import com.shop.ecommerce.service.VerificationCodeService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class VerificationCodeServiceImpl implements VerificationCodeService {
    private final VerificationCodeRepository verificationCodeRepository;
    @Override
    public VerificationCode createVerificationCode(String otp, String email) {
        VerificationCode verificationCodeExists = verificationCodeRepository.findByEmail(email);
        if (verificationCodeExists != null) {
            verificationCodeRepository.delete(verificationCodeExists);
        }
        VerificationCode verificationCode = new VerificationCode();
        verificationCode.setEmail(email);
        verificationCode.setOtp(otp);
        return verificationCodeRepository.save(verificationCode);
    }
}
