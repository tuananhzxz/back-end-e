package com.shop.ecommerce.controller;

import com.shop.ecommerce.exception.CommonException;
import com.shop.ecommerce.modal.Product;
import com.shop.ecommerce.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/product")
public class ProductController {

    private final ProductService productService;

    @GetMapping("{id}")
    public ResponseEntity<Object> getProductById(@PathVariable Long id) throws CommonException {
        Product product = productService.findProductById(id);
        return ResponseEntity.ok(product);
    }

    @GetMapping("/search")
    public ResponseEntity<List<?>> searchProduct(@RequestParam(required = false) String query) {
        return ResponseEntity.ok(productService.searchProduct(query));
    }

    @GetMapping
    public ResponseEntity<Page<?>> getAllProducts(@RequestParam(required = false) String category,
                                                       @RequestParam(required = false) String brand,
                                                       @RequestParam(required = false) String colors,
                                                       @RequestParam(required = false) String sizes,
                                                       @RequestParam(required = false) Integer minPrice,
                                                       @RequestParam(required = false) Integer maxPrice,
                                                       @RequestParam(required = false) Integer minDiscount,
                                                       @RequestParam(required = false) String sort,
                                                       @RequestParam(required = false) String stock,
                                                       @RequestParam(defaultValue = "0") Integer pageNumber) {
        return ResponseEntity.ok(productService.getAllProducts(category, brand, colors, sizes,
                minPrice, maxPrice, minDiscount, sort, stock, pageNumber));
    }

    @GetMapping("/getProducts")
    public ResponseEntity<List<?>> getProducts() {
        return ResponseEntity.ok(productService.getProducts());
    }

    @GetMapping("/similar")
    public ResponseEntity<List<?>> getSimilarProducts(
            @RequestParam Long categoryId,
            @RequestParam(required = false) Long currentProductId
    ) {
        List<Product> similarProducts = productService.getSimilarProducts(categoryId, currentProductId);
        return ResponseEntity.ok(similarProducts);
    }

}
