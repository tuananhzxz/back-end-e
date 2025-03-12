package com.shop.ecommerce.service.impl;

import com.shop.ecommerce.exception.CommonException;
import com.shop.ecommerce.modal.Category;
import com.shop.ecommerce.modal.Product;
import com.shop.ecommerce.modal.Seller;
import com.shop.ecommerce.repository.CartItemRepository;
import com.shop.ecommerce.repository.ProductRepository;
import com.shop.ecommerce.request.CreateProductRequest;
import com.shop.ecommerce.service.CartItemService;
import com.shop.ecommerce.service.ProductService;
import com.shop.ecommerce.utils.CategoryUtils;
import com.shop.ecommerce.utils.MessageMultiUtils;
import com.shop.ecommerce.utils.UpdateUtils;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.Predicate;
import lombok.RequiredArgsConstructor;
import org.apache.juli.logging.Log;
import org.apache.juli.logging.LogFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {
    private final ProductRepository productRepository;
    private final CategoryUtils categoryUtils;
    private final MessageMultiUtils messageMultiUtils;
    private final CartItemService cartItemService;
    private static final Log log = LogFactory.getLog(ProductServiceImpl.class);
    @Override
    public Product createProduct(CreateProductRequest req, Seller seller) throws CommonException {
        Category category = categoryUtils.getOrCreateCategory(req.getCategory(), 1, null);
        Category category2 = categoryUtils.getOrCreateCategory(req.getCategory2(), 2, category);
        Category category3 = categoryUtils.getOrCreateCategory(req.getCategory3(), 3, category2);

        Integer discount = this.calculateDiscount(req.getMrpPrice(), req.getSellingPrice());

        Product product = new Product();
        product.setCategory(category3);
        UpdateUtils.updateNonNullFields(product, req, new HashSet<>());
        product.setDiscountPercent(discount);
        product.setSeller(seller);

        return productRepository.save(product);
    }

    private Integer calculateDiscount(Integer mrpPrice, Integer sellingPrice) {
        if (mrpPrice <= 0) throw new IllegalArgumentException(messageMultiUtils.getMessage("mrp.price.invalid"));
        if (sellingPrice <= 0) throw new IllegalArgumentException(messageMultiUtils.getMessage("selling.price.invalid"));
        double discount = mrpPrice - sellingPrice;
        return (int) ((discount / mrpPrice) * 100);
    }

    public void deleteProduct(Long productId) throws CommonException {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new CommonException(messageMultiUtils.getMessage("product.not.found")));

        cartItemService.deleteByProductId(product.getId());

        productRepository.delete(product);
    }

    @Override
    public Product updateProduct(Long productId, Product product) throws CommonException {
        Product product1 = this.findProductById(productId);
        product.setId(productId);
        product.setCategory(product1.getCategory());
        product.setSeller(product1.getSeller());
        product.setReviews(product1.getReviews());
        return productRepository.save(product);
    }

    @Override
    public Product findProductById(Long productId) throws CommonException {
        return productRepository.findById(productId).orElseThrow(() -> new CommonException(messageMultiUtils.getMessage("product.not.found")));
    }

    @Override
    public List<Product> searchProduct(String query) {
        return productRepository.searchProduct(query);
    }

    @Override
    public Page<Product> getAllProducts(String category, String brand, String color, String sizes, Integer minPrice,
                                        Integer maxPrice, Integer minDiscount,String sort, String stock, Integer pageNumber) {
        Specification<Product> specification = (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();
            if (category != null) {
                Join<Product, Category> categoryJoin = root.join("category");
                predicates.add(criteriaBuilder.equal(categoryJoin.get("categoryId"), category));
            }

            if (color != null && !color.isEmpty()) predicates.add(criteriaBuilder.equal(root.get("color"), color));
            if (sizes != null && !sizes.isEmpty()) predicates.add(criteriaBuilder.equal(root.get("sizes"), sizes));
            if (minPrice != null) predicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get("sellingPrice"), minPrice));
            if (maxPrice != null) predicates.add(criteriaBuilder.lessThanOrEqualTo(root.get("sellingPrice"), maxPrice));
            if (minDiscount != null) predicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get("discountPercent"), minDiscount));
            if (stock != null) predicates.add(criteriaBuilder.equal(root.get("stock"), stock));
            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };

        Pageable pageable;
        if (sort != null && !sort.isEmpty()) {
            pageable = switch (sort) {
                case "price_low" ->
                        PageRequest.of(pageNumber != null ? pageNumber : 0, 10, Sort.by("sellingPrice").ascending());
                case "price_high" ->
                        PageRequest.of(pageNumber != null ? pageNumber : 0, 10, Sort.by("sellingPrice").descending());
                default -> PageRequest.of(pageNumber != null ? pageNumber : 0, 10, Sort.unsorted());
            };
        } else pageable = PageRequest.of(pageNumber != null ? pageNumber : 0, 10, Sort.unsorted());
        return productRepository.findAll(specification, pageable);
    }

    @Override
    public List<Product> getProductsBySellerId(Long sellerId) {
        return productRepository.findBySellerId(sellerId);
    }
    @Override
    public List<Product> getSimilarProducts(Long categoryId, Long currentProductId) {
        try {
            if (currentProductId != null) {
                Product currentProduct = productRepository.findById(currentProductId)
                        .orElseThrow(() -> new CommonException(messageMultiUtils.getMessage("product.not.found")));

                Integer minPrice = (int) (currentProduct.getSellingPrice() * 0.8);
                Integer maxPrice = (int) (currentProduct.getSellingPrice() * 1.2);

                List<Product> similarProducts = productRepository.findSimilarProductsAdvanced(
                        categoryId,
                        currentProductId,
                        minPrice,
                        maxPrice,
                        currentProduct.getColor()
                );

                if (similarProducts.size() < 5) {
                    List<Product> additionalProducts = productRepository
                            .findSimilarProducts(categoryId, currentProductId);

                    for (Product product : additionalProducts) {
                        if (similarProducts.size() >= 5) break;
                        if (!similarProducts.contains(product)) {
                            similarProducts.add(product);
                        }
                    }
                }

                return similarProducts;
            }

            return productRepository.findByCategoryIdOrderByCreatedDateDesc(categoryId);

        } catch (Exception e) {
            log.error(messageMultiUtils.getMessage("errorSimilarProduct"), e);
            return Collections.emptyList();
        }
    }

    @Override
    public List<Product> getProducts() {
        return productRepository.findAll();
    }
}
