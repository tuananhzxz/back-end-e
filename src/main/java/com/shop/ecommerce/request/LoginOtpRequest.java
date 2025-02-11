package com.shop.ecommerce.request;

import com.shop.ecommerce.domain.User_Role;
import lombok.Data;

@Data
public class LoginOtpRequest {
    String email;
    String otp;
    User_Role role;
}
