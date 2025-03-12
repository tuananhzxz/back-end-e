package com.shop.ecommerce.repository;

import com.razorpay.Payment;
import com.shop.ecommerce.modal.PaymentOrder;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PaymentOrderRepository extends JpaRepository<PaymentOrder, Long> {
    PaymentOrder findByPaymentLinkId(String paymentLinkId);
    List<PaymentOrder> findByUserId(Long userId);
}
