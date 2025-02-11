package com.shop.ecommerce.service.impl;

import com.shop.ecommerce.modal.HomeCategory;
import com.shop.ecommerce.repository.HomeCategoryRepository;
import com.shop.ecommerce.service.HomeCategoryService;
import com.shop.ecommerce.utils.MessageMultiUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class HomeCategoryServiceImpl implements HomeCategoryService {

    private final HomeCategoryRepository homeCategoryRepository;
    private final MessageMultiUtils messageMultiUtils;
    @Override
    public HomeCategory createHomeCategory(HomeCategory homeCategory) {
        return homeCategoryRepository.save(homeCategory);
    }

    @Override
    public List<HomeCategory> createsCategories(List<HomeCategory> homeCategories) {
        List<HomeCategory> existingCategories = homeCategoryRepository.findAll();
        if (existingCategories.isEmpty()) {
            return homeCategoryRepository.saveAll(homeCategories);
        } else {
            return homeCategoryRepository.saveAll(homeCategories);
        }
    }

    @Override
    public HomeCategory updateHomeCategory(HomeCategory homeCategory, Long id) {
        HomeCategory existingHomeCategory = homeCategoryRepository.findById(id).orElseThrow(() -> new RuntimeException(messageMultiUtils.getMessage("home.category.not.found")));
        if (homeCategory.getName() != null) existingHomeCategory.setName(homeCategory.getName());
        if (homeCategory.getImage() != null) existingHomeCategory.setImage(homeCategory.getImage());
        if (homeCategory.getCategoryId() != null) existingHomeCategory.setCategoryId(homeCategory.getCategoryId());
        return homeCategoryRepository.save(existingHomeCategory);
    }

    @Override
    public List<HomeCategory> getAllHomeCategories() {
        return homeCategoryRepository.findAll();
    }
}
