package com.shop.ecommerce.service;

import com.shop.ecommerce.modal.Product;
import com.shop.ecommerce.modal.User;
import com.shop.ecommerce.modal.Wishlist;

public interface WishListService {
    Wishlist createWishList(User user);
    Wishlist getWishListByUserId(User user);
    Wishlist addProductToWishList(User user, Product product);
    void removeProductFromWishList(User user, Product product);
}
