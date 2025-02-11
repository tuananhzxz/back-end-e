package com.shop.ecommerce.controller;

import com.shop.ecommerce.modal.Home;
import com.shop.ecommerce.modal.HomeCategory;
import com.shop.ecommerce.service.HomeCategoryService;
import com.shop.ecommerce.service.HomeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/home-category")
public class HomeCategoryController {

    private final HomeCategoryService homeCategoryService;
    private final HomeService homeService;

    @PostMapping("/categories")
    public ResponseEntity<Object> createHomeCategories(@RequestBody List<HomeCategory> homeCategories) {
        List<HomeCategory> newHomeCategories = homeCategoryService.createsCategories(homeCategories);
        Home home = homeService.createHomePageData(newHomeCategories);
        return ResponseEntity.accepted().body(home);
    }

    @GetMapping("/categories")
    public ResponseEntity<List<?>> getHomeCategory() {
        List<HomeCategory> homeCategories = homeCategoryService.getAllHomeCategories();
        return ResponseEntity.ok(homeCategories);
    }

    @PatchMapping("/admin/{id}")
    public ResponseEntity<Object> updateHomeCategory(@PathVariable Long id, @RequestBody HomeCategory homeCategory) {
        HomeCategory updatedHomeCategory = homeCategoryService.updateHomeCategory(homeCategory, id);
        return ResponseEntity.ok(updatedHomeCategory);
    }
}
