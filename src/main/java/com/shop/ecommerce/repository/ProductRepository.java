package com.shop.ecommerce.repository;

import com.shop.ecommerce.modal.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ProductRepository extends JpaRepository<Product, Long>, JpaSpecificationExecutor<Product> {

    List<Product> findBySellerId(Long sellerId);

    @Query("SELECT p FROM Product p WHERE (:query IS NULL OR lower(p.title) LIKE lower(concat('%',:query,'%') ) ) " +
            "OR (:query IS NULL OR lower(p.category.name) LIKE lower(concat('%',:query,'%') ))")
    List<Product> searchProduct(@Param("query") String query);

    @Query("SELECT p FROM Product p WHERE p.category.id = :categoryId AND p.id != :currentProductId " +
            "ORDER BY p.createdDate DESC")
    List<Product> findSimilarProducts(
            @Param("categoryId") Long categoryId,
            @Param("currentProductId") Long currentProductId
    );

    List<Product> findByCategoryIdOrderByCreatedDateDesc(Long categoryId);

    @Query("SELECT p FROM Product p WHERE p.category.id = :categoryId " +
            "AND p.id != :currentProductId " +
            "AND p.sellingPrice BETWEEN :minPrice AND :maxPrice " +
            "AND p.color LIKE %:color% " +
            "ORDER BY p.numRatings DESC, p.createdDate DESC")
    List<Product> findSimilarProductsAdvanced(
            @Param("categoryId") Long categoryId,
            @Param("currentProductId") Long currentProductId,
            @Param("minPrice") Integer minPrice,
            @Param("maxPrice") Integer maxPrice,
            @Param("color") String color
    );
}
