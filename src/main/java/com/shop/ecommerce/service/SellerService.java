package com.shop.ecommerce.service;

import com.shop.ecommerce.domain.AccountStatus;
import com.shop.ecommerce.exception.SellerException;
import com.shop.ecommerce.modal.Seller;

import java.util.List;

public interface SellerService {
    Seller getSellerProfile(String token) throws SellerException;
    Seller getSellerById(Long id) throws SellerException;
    Seller getSellerByEmail(String email) throws SellerException;
    List<Seller> getAllSellers(AccountStatus status) throws SellerException;
    Seller createSeller(Seller seller) throws SellerException;
    Seller updateSeller(Long id, Seller seller) throws SellerException;
    void deleteSeller(Long id) throws SellerException;
    Seller verifyEmail(String email, String otp) throws SellerException;
    Seller updateAccountStatus(Long id, AccountStatus status) throws SellerException;
}
