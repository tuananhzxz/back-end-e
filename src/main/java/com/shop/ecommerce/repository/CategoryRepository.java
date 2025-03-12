package com.shop.ecommerce.repository;

import com.shop.ecommerce.modal.Category;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CategoryRepository extends JpaRepository<Category, Long> {
    Category findByCategoryId(String categoryId);

    Category findCategoryByName(String name);
}
