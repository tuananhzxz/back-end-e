package com.shop.ecommerce.service;

import com.shop.ecommerce.modal.CartItem;

public interface CartItemService {

    CartItem updateCartItem(Long userId, Long id, CartItem cartItem);
    void deleteCartItem(Long userId, Long cartItemId);
    CartItem findCartItemById(Long id);
    void deleteByProductId(Long productId);
}
