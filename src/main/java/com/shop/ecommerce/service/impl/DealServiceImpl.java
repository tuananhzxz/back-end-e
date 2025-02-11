package com.shop.ecommerce.service.impl;

import com.shop.ecommerce.exception.CommonException;
import com.shop.ecommerce.modal.Deal;
import com.shop.ecommerce.modal.HomeCategory;
import com.shop.ecommerce.repository.DealRepository;
import com.shop.ecommerce.repository.HomeCategoryRepository;
import com.shop.ecommerce.service.DealService;
import com.shop.ecommerce.utils.MessageMultiUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class DealServiceImpl implements DealService {

    private final DealRepository dealRepository;
    private final HomeCategoryRepository homeCategoryRepository;
    private final MessageMultiUtils messageMultiUtils;
    @Override
    public List<Deal> getDeal() {
        return dealRepository.findAll();
    }

    @Override
    public Deal createDeal(Deal deal) throws CommonException {
        HomeCategory homeCategory = homeCategoryRepository.findById(deal.getCategory().getId()).orElseThrow(() -> new CommonException(messageMultiUtils.getMessage("home.category.not.found")));
        Deal newDeal = dealRepository.save(deal);
        newDeal.setCategory(homeCategory);
        newDeal.setDiscount(deal.getDiscount());
        return dealRepository.save(newDeal);
    }

    @Override
    public Deal updateDeal(Deal deal, Long id) throws CommonException {
        Deal exsistingDeal = dealRepository.findById(id).orElseThrow(() -> new CommonException(messageMultiUtils.getMessage("deal.not.found")));
        HomeCategory homeCategory = homeCategoryRepository.findById(deal.getCategory().getId()).orElseThrow(() -> new CommonException(messageMultiUtils.getMessage("home.category.not.found")));
        if (exsistingDeal != null) {
            if (deal.getDiscount() != null) exsistingDeal.setDiscount(deal.getDiscount());
            if (deal.getCategory() != null) exsistingDeal.setCategory(homeCategory);
            return dealRepository.save(exsistingDeal);
        }
        throw new CommonException(messageMultiUtils.getMessage("deal.not.found"));
    }

    @Override
    public void deleteDeal(Long dealId) throws CommonException {
        Deal deal = dealRepository.findById(dealId).orElseThrow(() -> new CommonException(messageMultiUtils.getMessage("deal.not.found")));
        dealRepository.delete(deal);
    }
}
