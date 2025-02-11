package com.shop.ecommerce.service.impl;

import com.shop.ecommerce.config.JwtProvider;
import com.shop.ecommerce.domain.User_Role;
import com.shop.ecommerce.modal.Cart;
import com.shop.ecommerce.modal.Seller;
import com.shop.ecommerce.modal.User;
import com.shop.ecommerce.modal.VerificationCode;
import com.shop.ecommerce.repository.CartRepository;
import com.shop.ecommerce.repository.SellerRepository;
import com.shop.ecommerce.repository.UserRepository;
import com.shop.ecommerce.repository.VerificationCodeRepository;
import com.shop.ecommerce.request.LoginRequest;
import com.shop.ecommerce.response.AuthResponse;
import com.shop.ecommerce.response.SignUpRequest;
import com.shop.ecommerce.service.AuthService;
import com.shop.ecommerce.service.EmailService;
import com.shop.ecommerce.utils.MessageMultiUtils;
import com.shop.ecommerce.utils.OtpUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final CartRepository cartRepository;
    private final JwtProvider jwtProvider;
    private final VerificationCodeRepository verificationCodeRepository;
    private final EmailService emailService;
    private final CustomUserServiceImpl customUserServiceImpl;
    private final SellerRepository sellerRepository;
    private final MessageMultiUtils messageMultiUtils;

    @Override
    public void sentLoginOtp(String email, User_Role role) {
        String SIGNING_PREFIX = "SIGNING_";
        if (email.startsWith(SIGNING_PREFIX)) {
            email = email.substring(SIGNING_PREFIX.length());

            if (role == User_Role.ROLE_SELLER) {
                Seller seller = sellerRepository.findByEmail(email);
                if (seller == null) {
                    throw new RuntimeException(messageMultiUtils.getMessage("seller.not.found"));
                }
            } else {
                User user = userRepository.findByEmail(email);
                if (user == null) {
                    throw new RuntimeException(messageMultiUtils.getMessage("user.not.found"));
                }
            }
        }

        VerificationCode verificationCode = verificationCodeRepository.findByEmail(email);
        if (verificationCode != null) {
            verificationCodeRepository.delete(verificationCode);
        }

        String otp = OtpUtils.generateOtp();
        verificationCode = new VerificationCode();
        verificationCode.setEmail(email);
        verificationCode.setOtp(otp);
        verificationCodeRepository.save(verificationCode);

        String subject = messageMultiUtils.getMessage("otp.subject");
        String text = messageMultiUtils.getMessage("otp.login.text") + ' ' + otp;

        emailService.sendVerificationOtpEmail(email, otp, subject, text);

    }

    @Override
    public String createUser(SignUpRequest req) {

        VerificationCode verificationCode = verificationCodeRepository.findByEmail(req.getEmail());
        if (verificationCode == null || !verificationCode.getOtp().equals(req.getOtp())) {
            throw new RuntimeException("otp.invalid");
        }

        User user = userRepository.findByEmail(req.getEmail());

        if (user == null) {
            user = new User();
            user.setEmail(req.getEmail());
            user.setFullName(req.getFullName());
            user.setRole(User_Role.ROLE_CUSTOMER);
            user.setPassword(passwordEncoder.encode(req.getOtp()));
            userRepository.save(user);

            Cart cart = new Cart();
            cart.setUser(user);
            cartRepository.save(cart);
        }

        List<GrantedAuthority> authorities = new ArrayList<>();
        authorities.add(new SimpleGrantedAuthority(User_Role.ROLE_CUSTOMER.toString()));

        Authentication authentication = new UsernamePasswordAuthenticationToken(req.getEmail(), null, authorities);
        SecurityContextHolder.getContext().setAuthentication(authentication);

        return jwtProvider.generateToken(authentication);
    }

    @Override
    public AuthResponse login(LoginRequest req) {
        String username = req.getEmail();
        String otp = req.getOtp();
        
        Authentication authentication = authenticate(username, otp);
        SecurityContextHolder.getContext().setAuthentication(authentication);

        String token = jwtProvider.generateToken(authentication);

        AuthResponse authResponse = new AuthResponse();
        authResponse.setJwt(token);
        authResponse.setMessage(messageMultiUtils.getMessage("login.success"));

        Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
        String role = authorities.isEmpty() ? null : authorities.iterator().next().getAuthority();

        authResponse.setRole(User_Role.valueOf(role));
        return authResponse;
    }

    private Authentication authenticate(String username, String otp) {
        UserDetails userDetails = customUserServiceImpl.loadUserByUsername(username);

        String PREFIX = "SELLER_";
        if(username.startsWith(PREFIX)) {
            username = username.substring(PREFIX.length());
        }

        if (userDetails == null) {
            throw new BadCredentialsException(messageMultiUtils.getMessage("user.not.found"));
        }

        VerificationCode verificationCode = verificationCodeRepository.findByEmail(username);
        if (verificationCode == null || !verificationCode.getOtp().equals(otp)) {
            throw new BadCredentialsException(messageMultiUtils.getMessage("otp.invalid"));
        }

        return new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
    }
}
