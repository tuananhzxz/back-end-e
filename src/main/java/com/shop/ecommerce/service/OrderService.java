package com.shop.ecommerce.service;

import com.shop.ecommerce.domain.OrderStatus;
import com.shop.ecommerce.exception.CommonException;
import com.shop.ecommerce.modal.*;

import java.util.List;
import java.util.Set;

public interface OrderService {
    Set<Order> createOrder(User user, Address shipAddress, Cart cart);
    Order findOrderById(Long id) throws CommonException;
    List<Order> usersOrderHistory(Long userId);
    List<Order> sellersOrder(Long sellerId);
    Order updateOrderStatus(Long orderId, OrderStatus status) throws CommonException;
    Order cancelOrder(Long orderId, User user) throws CommonException;
    OrderItem getOrderItemById(Long id) throws CommonException;
}
