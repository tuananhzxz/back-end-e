package com.shop.ecommerce.controller;

import com.shop.ecommerce.exception.SellerException;
import com.shop.ecommerce.modal.Seller;
import com.shop.ecommerce.modal.SellerReport;
import com.shop.ecommerce.service.SellerReportService;
import com.shop.ecommerce.service.SellerService;
import com.shop.ecommerce.utils.JWT_CONSTANT;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/seller-report")
@RequiredArgsConstructor
public class SellerReportController {
    private final SellerReportService sellerReportService;
    private final SellerService sellerService;

   @GetMapping
    public ResponseEntity<SellerReport> getSellerReport(@RequestHeader(JWT_CONSTANT.JWT_HEADER) String token) throws SellerException {
       Seller seller = sellerService.getSellerProfile(token);
       return ResponseEntity.ok(sellerReportService.getSellerReport(seller));
    }

}
