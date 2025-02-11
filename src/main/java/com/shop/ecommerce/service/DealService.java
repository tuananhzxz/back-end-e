package com.shop.ecommerce.service;

import com.shop.ecommerce.exception.CommonException;
import com.shop.ecommerce.modal.Deal;

import java.util.List;

public interface DealService {
    List<Deal> getDeal();
    Deal createDeal(Deal deal) throws CommonException;
    Deal updateDeal(Deal deal, Long id) throws CommonException;
    void deleteDeal(Long dealId) throws CommonException;
}
