package com.shop.ecommerce.service;

import com.shop.ecommerce.exception.CommonException;
import com.shop.ecommerce.modal.Product;
import com.shop.ecommerce.modal.Review;
import com.shop.ecommerce.modal.User;
import com.shop.ecommerce.request.CreateReviewRequest;

import java.util.List;

public interface ReviewService {
    Review createReview(CreateReviewRequest request, User user, Product product);
    List<Review> getReviewsByProductId(Long productId);
    Review updateReview(Long reviewId, String reviewText, Double rating, Long userId) throws CommonException;
    void deleteReview(Long reviewId, Long userId) throws CommonException;
    Review getReviewById(Long reviewId) throws CommonException;
}
