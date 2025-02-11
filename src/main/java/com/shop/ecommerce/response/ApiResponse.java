package com.shop.ecommerce.response;

import lombok.Data;

@Data
public class ApiResponse {
    private String message;
    private boolean isSuccess = true;
}
