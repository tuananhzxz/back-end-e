package com.shop.ecommerce.controller;

import com.shop.ecommerce.exception.SellerException;
import com.shop.ecommerce.modal.Seller;
import com.shop.ecommerce.modal.Transaction;
import com.shop.ecommerce.service.SellerService;
import com.shop.ecommerce.service.TransactionService;
import com.shop.ecommerce.utils.JWT_CONSTANT;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/transaction")
public class TransactionController {

    private final TransactionService transactionService;
    private final SellerService sellerService;

    @GetMapping("/seller")
    public ResponseEntity<List<?>> getTransactionsBySellerId(@RequestHeader(JWT_CONSTANT.JWT_HEADER) String token) throws SellerException {
        Seller seller = sellerService.getSellerProfile(token);
        return ResponseEntity.ok(transactionService.getTransactionsBySellerId(seller.getId()));
    }

    @GetMapping
    public ResponseEntity<List<?>> getAllTransactions() {
        return ResponseEntity.ok(transactionService.getAllTransactions());
    }
}
