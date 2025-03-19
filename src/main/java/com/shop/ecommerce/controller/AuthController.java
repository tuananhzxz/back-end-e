package com.shop.ecommerce.controller;

import com.shop.ecommerce.domain.User_Role;
import com.shop.ecommerce.modal.User;
import com.shop.ecommerce.modal.VerificationCode;
import com.shop.ecommerce.repository.UserRepository;
import com.shop.ecommerce.request.LoginOtpRequest;
import com.shop.ecommerce.request.LoginRequest;
import com.shop.ecommerce.response.ApiResponse;
import com.shop.ecommerce.response.AuthResponse;
import com.shop.ecommerce.response.SignUpRequest;
import com.shop.ecommerce.service.AuthService;
import com.shop.ecommerce.utils.JWT_CONSTANT;
import com.shop.ecommerce.utils.MessageMultiUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UserRepository userRepository;
    private final AuthService authService;
    private final MessageMultiUtils messageMultiUtils;

    @PostMapping("/signup")
    public ResponseEntity<Object> signUp(@RequestBody SignUpRequest req) {
        String jwt = authService.createUser(req);

        AuthResponse authResponse = new AuthResponse();
        authResponse.setJwt(jwt);
        authResponse.setMessage(messageMultiUtils.getMessage("user.created"));
        authResponse.setRole(User_Role.ROLE_CUSTOMER);

        return ResponseEntity.ok(authResponse);
    }

    @PostMapping("/sent/login/otp")
    public ResponseEntity<Object> sentOtp(@RequestBody LoginOtpRequest code) {
        authService.sentLoginOtp(code.getEmail(), code.getRole());

        ApiResponse response = new ApiResponse();
        response.setMessage(messageMultiUtils.getMessage("otp.sent"));

        return ResponseEntity.ok(response);
    }

    @PostMapping("/sent/login")
    public ResponseEntity<Object> login(@RequestBody LoginRequest loginRequest) {
        AuthResponse response = authService.login(loginRequest);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/validate-token")
    public ResponseEntity<Object> validateToken(@RequestHeader(JWT_CONSTANT.JWT_HEADER) String token) {
        VerificationCode verificationCode = authService.validateToken(token);

        ApiResponse response = new ApiResponse();
        response.setMessage("Token is valid");
        response.setData(verificationCode);

        return ResponseEntity.ok(response);
    }
}
