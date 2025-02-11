package com.shop.ecommerce.controller;

import com.shop.ecommerce.domain.AccountStatus;
import com.shop.ecommerce.exception.SellerException;
import com.shop.ecommerce.modal.Seller;
import com.shop.ecommerce.modal.VerificationCode;
import com.shop.ecommerce.repository.VerificationCodeRepository;
import com.shop.ecommerce.request.LoginRequest;
import com.shop.ecommerce.response.AuthResponse;
import com.shop.ecommerce.service.*;
import com.shop.ecommerce.utils.JWT_CONSTANT;
import com.shop.ecommerce.utils.MessageMultiUtils;
import com.shop.ecommerce.utils.OtpUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/seller")
public class SellerController {
    private final SellerService sellerService;
    private final AuthService authService;
    private final VerificationCodeRepository verificationCodeRepository;
    private final EmailService emailService;
    private final VerificationCodeService verificationCodeService;
    private final MessageMultiUtils messageMultiUtils;
    private final SellerReportService sellerReportService;

    @PostMapping("/login")
    public ResponseEntity<Object> loginSeller(@RequestBody LoginRequest code) throws SellerException {
        String email = code.getEmail();

        Seller seller = sellerService.getSellerByEmail(email);
        if (seller == null) {
            return ResponseEntity.badRequest().body(messageMultiUtils.getMessage("seller.not.found"));
        }

        if (!seller.getIsEmailVerified()) return ResponseEntity.badRequest().body(messageMultiUtils.getMessage("seller.email.not.verified"));

        code.setEmail("SELLER_" + email);
        AuthResponse authResponse = authService.login(code);
        return ResponseEntity.ok(authResponse);
    }

    @PatchMapping("/verify/{otp}")
    public ResponseEntity<Object> verifySeller(@PathVariable String otp) throws SellerException {
        VerificationCode verificationCode = verificationCodeRepository.findByOtp(otp);

        if (verificationCode == null || !verificationCode.getOtp().equals(otp)) {
            return ResponseEntity.badRequest().body(messageMultiUtils.getMessage("seller.otp.invalid"));
        }

        Seller seller = sellerService.verifyEmail(verificationCode.getEmail(), otp);

        return ResponseEntity.ok(seller);
    }

    @PostMapping
    public ResponseEntity<Object> createSeller(@RequestBody Seller seller) throws SellerException {
        Seller newSeller = sellerService.createSeller(seller);

        String otp = OtpUtils.generateOtp();
        VerificationCode verificationCode = verificationCodeService.createVerificationCode(otp, seller.getEmail());

        String subject = messageMultiUtils.getMessage("seller.email.verification.subject");
        String text = messageMultiUtils.getMessage("seller.email.verification.text");
        String url = "http://localhost:8080/api/seller/verify/" + otp;
        emailService.sendVerificationOtpEmail(seller.getEmail(), verificationCode.getOtp(), subject, text + url);

        return ResponseEntity.status(HttpStatus.CREATED).body(newSeller);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Object> getSellerById(@PathVariable Long id) throws SellerException {
        Seller seller = sellerService.getSellerById(id);
        return ResponseEntity.ok(seller);
    }

    @GetMapping("/profile")
    public ResponseEntity<Object> getSellerProfile(@RequestHeader(JWT_CONSTANT.JWT_HEADER) String token) throws SellerException {
        Seller seller = sellerService.getSellerProfile(token);
        return ResponseEntity.ok(seller);
    }

    @GetMapping("/report")
    public ResponseEntity<Object> getSellerReport(@RequestHeader(JWT_CONSTANT.JWT_HEADER) String token) throws SellerException {
        Seller seller = sellerService.getSellerProfile(token);
        return ResponseEntity.ok(sellerReportService.getSellerReport(seller));
    }

    @GetMapping
    public ResponseEntity<Object> getAllSellers(@RequestParam(required = false) AccountStatus accountStatus) throws SellerException {
        return ResponseEntity.ok(sellerService.getAllSellers(accountStatus));
    }

    @PatchMapping("/update")
    public ResponseEntity<Object> updateSeller(@RequestHeader(JWT_CONSTANT.JWT_HEADER) String token, @RequestBody Seller seller) throws SellerException {
        Seller profile = sellerService.getSellerProfile(token);
        Seller updatedSeller = sellerService.updateSeller(profile.getId(), seller);
        return ResponseEntity.ok(updatedSeller);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Object> deleteSeller(@PathVariable Long id) throws SellerException {
        sellerService.deleteSeller(id);
        return ResponseEntity.ok(messageMultiUtils.getMessage("seller.deleted"));
    }
}
