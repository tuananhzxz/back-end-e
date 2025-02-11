package com.shop.ecommerce.request;

import lombok.Data;

@Data
public class AddItemRequest {
    private Long productId;
    private String size;
    private Integer quantity;
}
