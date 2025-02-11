package com.shop.ecommerce.controller;

import com.shop.ecommerce.domain.OrderStatus;
import com.shop.ecommerce.exception.CommonException;
import com.shop.ecommerce.exception.SellerException;
import com.shop.ecommerce.modal.Order;
import com.shop.ecommerce.modal.Seller;
import com.shop.ecommerce.service.OrderService;
import com.shop.ecommerce.service.SellerService;
import com.shop.ecommerce.utils.JWT_CONSTANT;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/seller/order")
public class SellerOrderController {

    private final OrderService orderService;
    private final SellerService sellerService;

    @GetMapping
    public ResponseEntity<List<?>> getSellerOrders(@RequestHeader(JWT_CONSTANT.JWT_HEADER) String token) throws SellerException {
        Seller seller = sellerService.getSellerProfile(token);
        List<Order> orders = orderService.sellersOrder(seller.getId());
        return ResponseEntity.ok(orders);
    }

    @PatchMapping("/{orderId}/status/{orderStatus}")
    public ResponseEntity<?> updateOrderStatus(@PathVariable Long orderId, @PathVariable OrderStatus orderStatus, @RequestHeader(JWT_CONSTANT.JWT_HEADER) String token) throws SellerException, CommonException {
        sellerService.getSellerProfile(token);
        Order order = orderService.updateOrderStatus(orderId, orderStatus);
        return ResponseEntity.accepted().body(order);
    }
}
