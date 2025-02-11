package com.shop.ecommerce.controller;

import com.shop.ecommerce.exception.CommonException;
import com.shop.ecommerce.exception.SellerException;
import com.shop.ecommerce.modal.Product;
import com.shop.ecommerce.modal.Seller;
import com.shop.ecommerce.request.CreateProductRequest;
import com.shop.ecommerce.service.ProductService;
import com.shop.ecommerce.service.SellerService;
import com.shop.ecommerce.utils.JWT_CONSTANT;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/seller/product")
public class SellerProductController {

    private final ProductService productService;
    private final SellerService sellerService;

    @GetMapping
    public ResponseEntity<List<?>> getProductsBySellerId(@RequestHeader(JWT_CONSTANT.JWT_HEADER) String token) throws SellerException {
        Seller seller = sellerService.getSellerProfile(token);
        List<Product> products = productService.getProductsBySellerId(seller.getId());
        return ResponseEntity.ok(products);
    }

    @PostMapping
    public ResponseEntity<Object> createProduct(@RequestHeader(JWT_CONSTANT.JWT_HEADER) String token,
                                                 @RequestBody CreateProductRequest request) throws SellerException, CommonException {
        Seller seller = sellerService.getSellerProfile(token);
        Product product = productService.createProduct(request, seller);
        return new ResponseEntity<>(product, HttpStatus.CREATED);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProduct(@PathVariable Long id) {
        try {
            productService.deleteProduct(id);
            return ResponseEntity.noContent().build();
        } catch (CommonException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<Object> updateProduct(@PathVariable Long id, @RequestBody Product product) throws CommonException {
            Product updatedProduct = productService.updateProduct(id, product);
            return ResponseEntity.ok(updatedProduct);
    }
}
