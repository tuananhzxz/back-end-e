package com.shop.ecommerce.controller;

import com.shop.ecommerce.modal.User;
import com.shop.ecommerce.service.UserService;
import com.shop.ecommerce.utils.JWT_CONSTANT;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping("/user/profile")
    public ResponseEntity<Object> getUserProfile(@RequestHeader(JWT_CONSTANT.JWT_HEADER) String token) {
        User user = userService.findUserByJwtToken(token);

        return ResponseEntity.ok(user);
    }

}
