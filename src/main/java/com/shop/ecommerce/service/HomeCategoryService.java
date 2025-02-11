package com.shop.ecommerce.service;

import com.shop.ecommerce.modal.Home;
import com.shop.ecommerce.modal.HomeCategory;

import java.util.List;

public interface HomeCategoryService {
    HomeCategory createHomeCategory(HomeCategory homeCategory);
    List<HomeCategory> createsCategories(List<HomeCategory> homeCategories);
    HomeCategory updateHomeCategory(HomeCategory homeCategory, Long id);
    List<HomeCategory> getAllHomeCategories();
}
