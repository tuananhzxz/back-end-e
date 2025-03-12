package com.shop.ecommerce.service.impl;

import com.shop.ecommerce.exception.CartException;
import com.shop.ecommerce.modal.Cart;
import com.shop.ecommerce.modal.CartItem;
import com.shop.ecommerce.modal.Product;
import com.shop.ecommerce.modal.User;
import com.shop.ecommerce.repository.CartItemRepository;
import com.shop.ecommerce.repository.CartRepository;
import com.shop.ecommerce.service.CartService;
import com.shop.ecommerce.utils.MessageMultiUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CartServiceImpl implements CartService {
    private final CartItemRepository cartItemRepository;
    private final CartRepository cartRepository;
    private final MessageMultiUtils messageMultiUtils;
    @Override
    public CartItem createCartItem(User user, Product product, String size, Integer quantity) throws CartException {
        Cart cart = findUserCart(user);
        return cartItemRepository.findByCartAndProductAndSize(cart, product, size)
                .orElseGet(() -> {
                    CartItem cartItem = new CartItem();
                    cartItem.setQuantity(quantity);
                    cartItem.setProduct(product);
                    cartItem.setSize(size);
                    cartItem.setUserId(user.getId());
                    cartItem.setSellingPrice(product.getSellingPrice() * quantity);
                    cartItem.setMrpPrice(product.getMrpPrice() * quantity);
                    cart.getCartItems().add(cartItem);
                    cartItem.setCart(cart);
                    return cartItemRepository.save(cartItem);
                });
    }

    @Override
    public Cart findUserCart(User user) throws CartException {
        Cart cart = cartRepository.findByUserId(user.getId());

        Integer totalPrice = 0;
        Integer totalSellingDiscount = 0;
        Integer totalItem = 0;

        for (CartItem cartItem : cart.getCartItems()) {
            totalPrice += cartItem.getMrpPrice();
            totalSellingDiscount += cartItem.getProduct().getSellingPrice() * cartItem.getQuantity();
            totalItem += cartItem.getQuantity();
        }

        cart.setTotalMrpPrice(totalPrice);
        cart.setTotalSellingPrice(totalSellingDiscount);
        cart.setTotalItems(totalItem);
//        cart.setDiscount(calculateDiscount(totalPrice, totalSellingDiscount));
        cart.setCurrentPrice(totalSellingDiscount);
        return cart;
    }

    @Override
    public void deleteCartItem(User user, Long cartItemId) throws CartException {
        Cart cart = findUserCart(user);
        CartItem cartItem = cartItemRepository.findById(cartItemId)
                .orElseThrow(() -> new CartException(messageMultiUtils.getMessage("cart.item.not.found")));
        cart.getCartItems().remove(cartItem);
        cartRepository.save(cart);
        cartItemRepository.delete(cartItem);
    }

    private Integer calculateDiscount(Integer mrpPrice, Integer sellingPrice) {
        if (mrpPrice <= 0) return 0;
        if (sellingPrice <= 0) return 0;
        double discount = mrpPrice - sellingPrice;
        return (int) ((discount / mrpPrice) * 100);
    }
}
