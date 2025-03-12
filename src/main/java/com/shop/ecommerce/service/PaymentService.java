package com.shop.ecommerce.service;

import com.razorpay.Payment;
import com.razorpay.PaymentLink;
import com.razorpay.RazorpayException;
import com.shop.ecommerce.domain.PaymentMethod;
import com.shop.ecommerce.exception.CommonException;
import com.shop.ecommerce.modal.Order;
import com.shop.ecommerce.modal.PaymentDetails;
import com.shop.ecommerce.modal.PaymentOrder;
import com.shop.ecommerce.modal.User;
import com.stripe.exception.StripeException;

import java.util.List;
import java.util.Set;

public interface PaymentService {

    PaymentOrder createOrder(User user, Set<Order> orders, PaymentMethod paymentMethod);
    PaymentOrder getPaymentOrderById(Long orderId) throws CommonException;
    List<PaymentOrder> getPaymentOrderByUserId(Long userId) throws CommonException;
    PaymentOrder getPaymentOrderByPaymentId(String paymentId) throws CommonException;
    Boolean proceedPaymentOrder(PaymentOrder paymentOrder, String paymentId, String paymentLinkId) throws RazorpayException, StripeException;
    PaymentLink createRazorpayPayment(User user, Long amount, Long orderId) throws RazorpayException;
    String createStripePaymentLink(User user, Long amount, Long orderId) throws StripeException;
}
