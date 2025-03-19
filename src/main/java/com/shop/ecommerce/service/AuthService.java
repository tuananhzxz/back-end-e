package com.shop.ecommerce.service;

import com.shop.ecommerce.domain.User_Role;
import com.shop.ecommerce.modal.User;
import com.shop.ecommerce.modal.VerificationCode;
import com.shop.ecommerce.request.LoginRequest;
import com.shop.ecommerce.response.AuthResponse;
import com.shop.ecommerce.response.SignUpRequest;

public interface AuthService {
    void sentLoginOtp(String email, User_Role role);
    String createUser(SignUpRequest req);
    AuthResponse login(LoginRequest req);
    VerificationCode validateToken(String token);
}
