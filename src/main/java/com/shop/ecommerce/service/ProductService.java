package com.shop.ecommerce.service;

import com.shop.ecommerce.exception.CommonException;
import com.shop.ecommerce.modal.Product;
import com.shop.ecommerce.modal.Seller;
import com.shop.ecommerce.request.CreateProductRequest;
import org.springframework.data.domain.Page;

import java.util.List;

public interface ProductService {

    Product createProduct(CreateProductRequest req, Seller seller) throws CommonException;
    void deleteProduct(Long productId) throws CommonException;
    Product updateProduct(Long productId, Product product) throws CommonException;
    Product findProductById(Long productId) throws CommonException;
    List<Product> searchProduct(String query);
    Page<Product> getAllProducts(String category, String brand, String colors, String sizes, Integer minPrice,
                                 Integer maxPrice, Integer minDiscount ,String sort, String stock, Integer pageNumber);
    List<Product> getProductsBySellerId(Long sellerId);
    List<Product> getSimilarProducts(Long categoryId, Long currentProductId);
    List<Product> getProducts();
}
