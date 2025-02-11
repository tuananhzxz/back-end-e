package com.shop.ecommerce.response;

import com.shop.ecommerce.domain.User_Role;
import lombok.Data;

@Data
public class AuthResponse {
    private String jwt;
    private String message;
    private User_Role role;
}
