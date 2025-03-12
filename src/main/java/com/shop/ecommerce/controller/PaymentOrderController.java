package com.shop.ecommerce.controller;

import com.shop.ecommerce.exception.CommonException;
import com.shop.ecommerce.exception.SellerException;
import com.shop.ecommerce.modal.PaymentOrder;
import com.shop.ecommerce.modal.Seller;
import com.shop.ecommerce.modal.Transaction;
import com.shop.ecommerce.service.PaymentService;
import com.shop.ecommerce.service.SellerService;
import com.shop.ecommerce.service.TransactionService;
import com.shop.ecommerce.utils.JWT_CONSTANT;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/payment-order")
public class PaymentOrderController {

    private final PaymentService paymentService;
    private final TransactionService transactionService;
    private final SellerService sellerService;

    @GetMapping
    public ResponseEntity<List<PaymentOrder>> getPaymentOrder(@RequestHeader(JWT_CONSTANT.JWT_HEADER) String token) throws CommonException, SellerException {
        Seller seller = sellerService.getSellerProfile(token);
        List<Transaction> transactions = transactionService.getTransactionsBySellerId(seller.getId());
        List<PaymentOrder> paymentOrders = new ArrayList<>();
        for (Transaction transaction : transactions) {
            paymentOrders = paymentService.getPaymentOrderByUserId(transaction.getCustomer().getId());
        }
        return ResponseEntity.ok(paymentOrders);
    }
}
