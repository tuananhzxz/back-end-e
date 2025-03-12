package com.shop.ecommerce.utils;

import com.shop.ecommerce.modal.Category;
import com.shop.ecommerce.repository.CategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.text.Normalizer;

@Component
@RequiredArgsConstructor
public class CategoryUtils {
    private final CategoryRepository categoryRepository;
    public Category getOrCreateCategory(String categoryId, int level, Category parentCategory) {
        Category category = categoryRepository.findCategoryByName(categoryId);
        if (category == null) {
            category = new Category();
            category.setName(categoryId);
            category.setCategoryId(convertToUnderscore(categoryId));
            category.setLevel(level);
            category.setParentCategory(parentCategory);
            category = categoryRepository.save(category);
        }
        return category;
    }

    public static String convertToUnderscore(String input) {
        // Bỏ dấu
        String normalized = Normalizer.normalize(input, Normalizer.Form.NFD);
        String noDiacritics = normalized.replaceAll("\\p{InCombiningDiacriticalMarks}+", "");

        // Thay thế khoảng trắng bằng dấu gạch dưới
        return noDiacritics.trim().replaceAll("\\s+", "_");
    }
}
