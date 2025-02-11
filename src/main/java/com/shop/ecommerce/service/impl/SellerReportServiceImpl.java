package com.shop.ecommerce.service.impl;

import com.shop.ecommerce.modal.Seller;
import com.shop.ecommerce.modal.SellerReport;
import com.shop.ecommerce.repository.SellerReportRepository;
import com.shop.ecommerce.service.SellerReportService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SellerReportServiceImpl implements SellerReportService {
    private final SellerReportRepository sellerReportRepository;

    @Override
    public SellerReport getSellerReport(Seller seller) {
        SellerReport sellerReport = sellerReportRepository.findBySellerId(seller.getId());
        if (sellerReport == null) {
            SellerReport newSeller = new SellerReport();
            newSeller.setSeller(seller);
            return sellerReportRepository.save(newSeller);
        }
        return sellerReport;
    }

    @Override
    public SellerReport updateSellerReport(SellerReport sellerReport) {
        return sellerReportRepository.save(sellerReport);
    }
}
