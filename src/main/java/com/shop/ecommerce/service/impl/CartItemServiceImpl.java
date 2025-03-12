package com.shop.ecommerce.service.impl;

import com.shop.ecommerce.modal.Cart;
import com.shop.ecommerce.modal.CartItem;
import com.shop.ecommerce.modal.User;
import com.shop.ecommerce.repository.CartItemRepository;
import com.shop.ecommerce.repository.CartRepository;
import com.shop.ecommerce.service.CartItemService;
import com.shop.ecommerce.utils.MessageMultiUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CartItemServiceImpl implements CartItemService {
    private final CartItemRepository cartItemRepository;
    private final CartRepository cartRepository;
    private final MessageMultiUtils messageMultiUtils;
    @Override
    public CartItem updateCartItem(Long userId, Long id, CartItem cartItem) {
        CartItem item = this.findCartItemById(id);
        Cart cart = item.getCart();
        if (cart == null) {
            throw new IllegalArgumentException(messageMultiUtils.getMessage("cart.item.cart.null"));
        }
        User cartItemUser = cart.getUser();
        if (cartItemUser == null) {
            throw new IllegalArgumentException(messageMultiUtils.getMessage("cart.item.user.null"));
        }
        if (cartItemUser.getId().equals(userId)) {
            cart.setTotalSellingPrice(cart.getTotalSellingPrice() - item.getSellingPrice() + item.getProduct().getSellingPrice() * cartItem.getQuantity());
            cart.setTotalMrpPrice(cart.getTotalMrpPrice() - item.getMrpPrice() + item.getProduct().getMrpPrice() * cartItem.getQuantity());
            cart.setTotalItems(cart.getTotalItems() - item.getQuantity() + cartItem.getQuantity());
            cartRepository.save(cart);
            item.setQuantity(cartItem.getQuantity());
            item.setMrpPrice(item.getProduct().getMrpPrice() * cartItem.getQuantity());
            item.setSellingPrice(item.getProduct().getSellingPrice() * item.getQuantity());
            return cartItemRepository.save(item);
        }
        throw new IllegalArgumentException(messageMultiUtils.getMessage("cart.item.not.updated"));
    }
    @Override
    public void deleteCartItem(Long userId, Long cartItemId) {
        CartItem item = cartItemRepository.findCartItemById(cartItemId);
        User cartItemUser = item.getCart().getUser();
        if (cartItemUser.getId().equals(userId)) {
            cartItemRepository.delete(item);
        }else {
            throw new IllegalArgumentException(messageMultiUtils.getMessage("cart.item.not.deleted"));
        }
    }

    @Override
    public void deleteByProductId(Long productId) {
        CartItem item = cartItemRepository.findCartItemByProductId(productId);
        if (item != null) {
            cartItemRepository.delete(item);
        }
    }

    @Override
    public CartItem findCartItemById(Long id) {
        return cartItemRepository.findCartItemById(id);
    }
}
