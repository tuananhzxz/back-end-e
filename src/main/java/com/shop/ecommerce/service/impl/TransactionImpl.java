package com.shop.ecommerce.service.impl;

import com.shop.ecommerce.modal.Order;
import com.shop.ecommerce.modal.Seller;
import com.shop.ecommerce.modal.Transaction;
import com.shop.ecommerce.repository.SellerRepository;
import com.shop.ecommerce.repository.TransactionRepository;
import com.shop.ecommerce.service.TransactionService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class TransactionImpl implements TransactionService {
    private final TransactionRepository transactionRepository;
    private final SellerRepository sellerRepository;
    @Override
    public Transaction createTransaction(Order order) {
        Seller seller = sellerRepository.findById(order.getSellerId()).orElseThrow();

        Transaction transaction = Transaction.builder()
                .date(LocalDateTime.now())
                .customer(order.getUser())
                .order(order)
                .seller(seller)
                .build();
        return transactionRepository.save(transaction);
    }

    @Override
    public List<Transaction> getTransactionsBySellerId(Long sellerId) {
        return transactionRepository.findBySellerId(sellerId);
    }

    @Override
    public List<Transaction> getAllTransactions() {
        return transactionRepository.findAll();
    }
}
