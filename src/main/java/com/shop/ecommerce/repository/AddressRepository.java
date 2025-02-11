package com.shop.ecommerce.repository;

import com.shop.ecommerce.modal.Address;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AddressRepository extends JpaRepository<Address, Long> {
}
