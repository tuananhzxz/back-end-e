package com.shop.ecommerce.service;

import com.shop.ecommerce.modal.Seller;
import com.shop.ecommerce.modal.SellerReport;

public interface SellerReportService {
    SellerReport getSellerReport(Seller seller);
    SellerReport updateSellerReport(SellerReport sellerReport);
}
