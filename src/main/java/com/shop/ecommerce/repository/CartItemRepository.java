package com.shop.ecommerce.repository;

import com.shop.ecommerce.modal.Cart;
import com.shop.ecommerce.modal.CartItem;
import com.shop.ecommerce.modal.Product;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CartItemRepository extends JpaRepository<CartItem, Long> {
    Optional<CartItem> findByCartAndProductAndSize(Cart cart, Product product, String size);
    CartItem findCartItemById(Long cartItemId);
    CartItem findCartItemByProductId(Long productId);
}
