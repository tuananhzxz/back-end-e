package com.shop.ecommerce.repository;

import com.shop.ecommerce.modal.HomeCategory;
import org.springframework.data.jpa.repository.JpaRepository;

public interface HomeCategoryRepository extends JpaRepository<HomeCategory, Long> {
}
