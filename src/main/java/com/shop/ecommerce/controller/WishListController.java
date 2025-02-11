package com.shop.ecommerce.controller;

import com.shop.ecommerce.exception.CommonException;
import com.shop.ecommerce.modal.Product;
import com.shop.ecommerce.modal.User;
import com.shop.ecommerce.service.ProductService;
import com.shop.ecommerce.service.UserService;
import com.shop.ecommerce.service.WishListService;
import com.shop.ecommerce.utils.JWT_CONSTANT;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/wishlist")
public class WishListController {

    private final WishListService wishListService;
    private final UserService userService;
    private final ProductService productService;

    @GetMapping
    public ResponseEntity<Object> getWishListByUserId(@RequestHeader(JWT_CONSTANT.JWT_HEADER) String token) {
        User user = userService.findUserByJwtToken(token);
        return ResponseEntity.ok(wishListService.getWishListByUserId(user));
    }

    @PostMapping("/add/product/{productId}")
    public ResponseEntity<Object> addProductToWishList(@RequestHeader(JWT_CONSTANT.JWT_HEADER) String token, @PathVariable Long productId) throws CommonException {
        Product product = productService.findProductById(productId);
        User user = userService.findUserByJwtToken(token);
        return ResponseEntity.ok(wishListService.addProductToWishList(user, product));
    }

    @DeleteMapping("/remove/product/{productId}")
    public ResponseEntity<Object> removeProductFromWishList(@RequestHeader(JWT_CONSTANT.JWT_HEADER) String token, @PathVariable Long productId) throws CommonException {
        Product product = productService.findProductById(productId);
        User user = userService.findUserByJwtToken(token);
        wishListService.removeProductFromWishList(user, product);
        return ResponseEntity.ok().build();
    }
}
