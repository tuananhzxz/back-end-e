package com.shop.ecommerce.service;

import com.shop.ecommerce.modal.User;

public interface UserService {
    User findUserByJwtToken(String token);
    User findUserByEmail(String email);
}
