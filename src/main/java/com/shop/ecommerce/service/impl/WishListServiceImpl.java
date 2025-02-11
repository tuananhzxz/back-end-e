package com.shop.ecommerce.service.impl;

import com.shop.ecommerce.modal.Product;
import com.shop.ecommerce.modal.User;
import com.shop.ecommerce.modal.Wishlist;
import com.shop.ecommerce.repository.WishListRepository;
import com.shop.ecommerce.service.WishListService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class WishListServiceImpl implements WishListService {
    private final WishListRepository wishListRepository;
    @Override
    public Wishlist createWishList(User user) {
        Wishlist wishlist = Wishlist.builder().user(user).build();
        return wishListRepository.save(wishlist);
    }

    @Override
    public Wishlist getWishListByUserId(User user) {
        Wishlist wishlist = wishListRepository.findByUserId(user.getId());
        if (wishlist == null) {
            return createWishList(user);
        }
        return wishlist;
    }

    @Override
    public Wishlist addProductToWishList(User user, Product product) {
        Wishlist wishlist = getWishListByUserId(user);

        if (wishlist.getProducts().contains(product)) {
            wishlist.getProducts().remove(product);
        } else {
            wishlist.getProducts().add(product);
        }
        return wishListRepository.save(wishlist);
    }

    @Override
    public void removeProductFromWishList(User user, Product product) {
        Wishlist wishlist = getWishListByUserId(user);
        wishlist.getProducts().remove(product);
        wishListRepository.save(wishlist);
    }
}
