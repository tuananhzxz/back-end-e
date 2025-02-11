package com.shop.ecommerce.repository;

import com.shop.ecommerce.modal.Address;
import com.shop.ecommerce.modal.Order;
import com.shop.ecommerce.modal.OrderItem;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {
}
