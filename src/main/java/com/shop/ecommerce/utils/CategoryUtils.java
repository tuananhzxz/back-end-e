package com.shop.ecommerce.utils;

import com.shop.ecommerce.modal.Category;
import com.shop.ecommerce.repository.CategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CategoryUtils {
    private final CategoryRepository categoryRepository;
    public Category getOrCreateCategory(String categoryId, int level, Category parentCategory) {
        Category category = categoryRepository.findByCategoryId(categoryId);
        if (category == null) {
            category = new Category();
            category.setCategoryId(categoryId);
            category.setLevel(level);
            category.setParentCategory(parentCategory);
            category = categoryRepository.save(category);
        }
        return category;
    }
}
