package com.shop.ecommerce.repository;

import com.shop.ecommerce.modal.Wishlist;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface WishListRepository extends JpaRepository<Wishlist, Long> {
    Wishlist findByUserId(Long userId);
}
