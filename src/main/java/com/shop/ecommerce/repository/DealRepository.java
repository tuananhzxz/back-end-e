package com.shop.ecommerce.repository;

import com.shop.ecommerce.modal.Deal;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DealRepository extends JpaRepository<Deal, Long> {
}
