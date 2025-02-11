package com.shop.ecommerce.controller;

import com.razorpay.PaymentLink;
import com.razorpay.RazorpayException;
import com.shop.ecommerce.domain.PaymentMethod;
import com.shop.ecommerce.exception.CartException;
import com.shop.ecommerce.exception.CommonException;
import com.shop.ecommerce.exception.SellerException;
import com.shop.ecommerce.modal.*;
import com.shop.ecommerce.repository.PaymentOrderRepository;
import com.shop.ecommerce.response.PaymentLinkResponse;
import com.shop.ecommerce.service.*;
import com.shop.ecommerce.utils.JWT_CONSTANT;
import com.stripe.exception.StripeException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/order")
public class OrderController {

    private final OrderService orderService;
    private final CartService cartService;
    private final UserService userService;
    private final SellerService sellerService;
    private final SellerReportService sellerReportService;
    private final PaymentService paymentService;
    private final PaymentOrderRepository paymentOrderRepository;

    @PostMapping("/create")
    public ResponseEntity<Object> createOrder(@RequestBody Address shippingAddress, @RequestParam PaymentMethod paymentMethod
            , @RequestHeader(JWT_CONSTANT.JWT_HEADER) String token) throws CartException, RazorpayException, StripeException {
        User user = userService.findUserByJwtToken(token);
        Cart cart = cartService.findUserCart(user);
        Set<Order> orders = orderService.createOrder(user, shippingAddress, cart);

        PaymentOrder paymentOrder = paymentService.createOrder(user, orders, paymentMethod);
        PaymentLinkResponse res = new PaymentLinkResponse();

        if (paymentMethod.equals(PaymentMethod.RAZORPAY)) {
            PaymentLink payment = paymentService.createRazorpayPayment(user, paymentOrder.getAmount(), paymentOrder.getId());
            String paymentUrl = payment.get("short_url");
            String paymentUrlId = payment.get("id");

            res.setPayment_link_url(paymentUrl);
            paymentOrder.setPaymentLinkId(paymentUrlId);
            paymentOrderRepository.save(paymentOrder);
        } else {
            String paymentUrl = paymentService.createStripePaymentLink(user, paymentOrder.getAmount(), paymentOrder.getId());
            String paymentUrlId = paymentUrl.split("/")[5];
            paymentOrder.setPaymentLinkId(paymentUrlId);
            paymentOrderRepository.save(paymentOrder);
            res.setPayment_link_url(paymentUrl);
        }
        return ResponseEntity.ok(res);
    }

    @GetMapping("/user")
    public ResponseEntity<List<?>> usersOrderHistory(@RequestHeader(JWT_CONSTANT.JWT_HEADER) String token) {
        User user = userService.findUserByJwtToken(token);
        return ResponseEntity.accepted().body(orderService.usersOrderHistory(user.getId()));
    }

    @GetMapping("/{userId}")
    public ResponseEntity<List<?>> usersOrderHistory(@PathVariable Long userId) {
        return ResponseEntity.accepted().body(orderService.usersOrderHistory(userId));
    }

    @GetMapping("/details/{orderId}")
    public ResponseEntity<?> getOrderById(@PathVariable Long orderId, @RequestHeader(JWT_CONSTANT.JWT_HEADER) String token) throws CommonException {
        userService.findUserByJwtToken(token);
        return ResponseEntity.ok(orderService.findOrderById(orderId));
    }

    @GetMapping("/item/{orderItemId}")
    public ResponseEntity<?> getOrderItemById(@PathVariable Long orderItemId, @RequestHeader(JWT_CONSTANT.JWT_HEADER) String token) throws CommonException {
        userService.findUserByJwtToken(token);
        return ResponseEntity.ok(orderService.getOrderItemById(orderItemId));
    }

    @PutMapping("/{orderId}/cancel")
    public ResponseEntity<?> cancelOrder(@PathVariable Long orderId, @RequestHeader(JWT_CONSTANT.JWT_HEADER) String token) throws CommonException, SellerException {
        User user = userService.findUserByJwtToken(token);
        Order order = orderService.cancelOrder(orderId, user);

        Seller seller = sellerService.getSellerById(order.getSellerId());
        SellerReport report = sellerReportService.getSellerReport(seller);

        report.setCanceledOrders(report.getCanceledOrders() + 1);
        report.setTotalRefunds(report.getTotalRefunds() + order.getTotalSellingPrice());
        sellerReportService.updateSellerReport(report);

        return ResponseEntity.ok(order);
    }
}
