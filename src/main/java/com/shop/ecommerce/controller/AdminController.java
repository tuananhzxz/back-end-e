package com.shop.ecommerce.controller;

import com.shop.ecommerce.domain.AccountStatus;
import com.shop.ecommerce.exception.SellerException;
import com.shop.ecommerce.modal.Seller;
import com.shop.ecommerce.service.SellerService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/admin")
public class AdminController {

    private final SellerService sellerService;

    @PatchMapping("seller/{id}/status/{status}")
    public ResponseEntity<Object> updateSellerStatus(@PathVariable Long id, @PathVariable AccountStatus status) throws SellerException {
        Seller updateSeller = sellerService.updateAccountStatus(id, status);
        return ResponseEntity.ok(updateSeller);
    }
}
