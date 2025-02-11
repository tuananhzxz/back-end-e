package com.shop.ecommerce.controller;

import com.shop.ecommerce.exception.CommonException;
import com.shop.ecommerce.modal.Deal;
import com.shop.ecommerce.response.ApiResponse;
import com.shop.ecommerce.service.DealService;
import com.shop.ecommerce.utils.MessageMultiUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/deal")
public class DealController {

    private final DealService dealService;
    private final MessageMultiUtils messageMultiUtils;

    @GetMapping
    public ResponseEntity<Object> getAllDeals() {
        return ResponseEntity.ok(dealService.getDeal());
    }

    @PostMapping
    public ResponseEntity<Object> createDeal(@RequestBody Deal deal) throws CommonException {
        Deal newDeal = dealService.createDeal(deal);
        return ResponseEntity.accepted().body(newDeal);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<Object> updateDeal(@RequestBody Deal deal, @PathVariable Long id) throws CommonException {
        Deal updatedDeal = dealService.updateDeal(deal, id);
        return ResponseEntity.ok(updatedDeal);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Object> deleteDeal(@PathVariable Long id) throws CommonException {
        dealService.deleteDeal(id);
        ApiResponse apiResponse = new ApiResponse();
        apiResponse.setMessage(messageMultiUtils.getMessage("deal.delete.success"));
        return ResponseEntity.accepted().body(apiResponse);
    }
}
