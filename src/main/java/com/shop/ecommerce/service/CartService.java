package com.shop.ecommerce.service;

import com.shop.ecommerce.exception.CartException;
import com.shop.ecommerce.modal.Cart;
import com.shop.ecommerce.modal.CartItem;
import com.shop.ecommerce.modal.Product;
import com.shop.ecommerce.modal.User;

public interface CartService {
    CartItem createCartItem(User user, Product product, String size, Integer quantity) throws CartException;
    Cart findUserCart(User user) throws CartException;
}
