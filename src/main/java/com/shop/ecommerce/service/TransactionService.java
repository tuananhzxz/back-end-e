package com.shop.ecommerce.service;

import com.shop.ecommerce.modal.Order;
import com.shop.ecommerce.modal.Transaction;

import java.util.List;

public interface TransactionService {
    Transaction createTransaction(Order order);
    List<Transaction> getTransactionsBySellerId(Long sellerId);
    List<Transaction> getAllTransactions();
}
