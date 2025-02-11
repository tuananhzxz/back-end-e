package com.shop.ecommerce.controller;

import com.shop.ecommerce.exception.CartException;
import com.shop.ecommerce.exception.CommonException;
import com.shop.ecommerce.modal.Cart;
import com.shop.ecommerce.modal.CartItem;
import com.shop.ecommerce.modal.Product;
import com.shop.ecommerce.modal.User;
import com.shop.ecommerce.request.AddItemRequest;
import com.shop.ecommerce.response.ApiResponse;
import com.shop.ecommerce.service.CartItemService;
import com.shop.ecommerce.service.CartService;
import com.shop.ecommerce.service.ProductService;
import com.shop.ecommerce.service.UserService;
import com.shop.ecommerce.utils.JWT_CONSTANT;
import com.shop.ecommerce.utils.MessageMultiUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/cart")
public class CartController {
    private final CartService cartService;
    private final CartItemService cartItemService;
    private final UserService userService;
    private final ProductService productService;
    private final MessageMultiUtils messageMultiUtils;

    @GetMapping
    public ResponseEntity<Object> findUserCartHandle(@RequestHeader(JWT_CONSTANT.JWT_HEADER) String token) throws CartException {
        User user = userService.findUserByJwtToken(token);
        Cart cart = cartService.findUserCart(user);
        return ResponseEntity.accepted().body(cart);
    }

    @PutMapping("/add")
    public ResponseEntity<Object> addProductToCartHandle(@RequestHeader(JWT_CONSTANT.JWT_HEADER) String token,
                                                         @RequestBody AddItemRequest request) throws CartException, CommonException {
        User user = userService.findUserByJwtToken(token);
        Product product = productService.findProductById(request.getProductId());
        CartItem cartItem = cartService.createCartItem(user, product, request.getSize(), request.getQuantity());
        ApiResponse response = new ApiResponse();
        response.setMessage(messageMultiUtils.getMessage("cart.item.added"));
        return ResponseEntity.ok(cartItem);
    }

    @DeleteMapping("/remove/{id}")
    public ResponseEntity<Object> removeCartItemHandle(@PathVariable Long id, @RequestHeader(JWT_CONSTANT.JWT_HEADER) String token) {
        Long userId = userService.findUserByJwtToken(token).getId();
        cartItemService.deleteCartItem(userId, id);
        ApiResponse response = new ApiResponse();
        response.setMessage(messageMultiUtils.getMessage("cart.item.removed"));
        return ResponseEntity.ok(response);
    }

    @PutMapping("/update/{cartItemId}")
    public ResponseEntity<Object> updateCartItemHandle(@PathVariable Long cartItemId, @RequestHeader(JWT_CONSTANT.JWT_HEADER) String token,
                                                      @RequestBody CartItem cartItem) throws CartException {
        User user = userService.findUserByJwtToken(token);
        CartItem updatedCartItem = null;
        if (cartItem.getQuantity() > 0) {
            updatedCartItem = cartItemService.updateCartItem(user.getId(), cartItemId, cartItem);
        }
        return ResponseEntity.ok(updatedCartItem);
    }

}
