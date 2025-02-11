package com.shop.ecommerce.service.impl;

import com.shop.ecommerce.config.JwtProvider;
import com.shop.ecommerce.modal.User;
import com.shop.ecommerce.repository.UserRepository;
import com.shop.ecommerce.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final JwtProvider jwtProvider;
    @Override
    public User findUserByJwtToken(String token) {
        String email = jwtProvider.getEmailFromToken(token);
        return this.findUserByEmail(email);
    }

    @Override
    public User findUserByEmail(String email) {
        User user = userRepository.findByEmail(email);

        if (user == null) {
            throw new RuntimeException("User not found");
        }

        return user;
    }
}
