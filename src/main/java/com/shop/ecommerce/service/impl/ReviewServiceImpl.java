package com.shop.ecommerce.service.impl;

import com.shop.ecommerce.exception.CommonException;
import com.shop.ecommerce.modal.Product;
import com.shop.ecommerce.modal.Review;
import com.shop.ecommerce.modal.User;
import com.shop.ecommerce.repository.ReviewRepository;
import com.shop.ecommerce.request.CreateReviewRequest;
import com.shop.ecommerce.service.ReviewService;
import com.shop.ecommerce.utils.MessageMultiUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
@Service
@RequiredArgsConstructor
public class ReviewServiceImpl implements ReviewService {

    private final ReviewRepository reviewRepository;
    private final MessageMultiUtils messageMultiUtils;
    @Override
    public Review createReview(CreateReviewRequest request, User user, Product product) {
        Review review = Review.builder()
                .user(user)
                .product(product)
                .reviewText(request.getReviewText())
                .rating(request.getReviewRating())
                .productImages(request.getProductImages())
                .createdDate(LocalDateTime.now())
                .build();

        product.getReviews().add(review);
        return reviewRepository.save(review);
    }
    @Override
    public List<Review> getReviewsByProductId(Long productId) {
        return reviewRepository.findByProductId(productId);
    }
    @Override
    public Review updateReview(Long reviewId, String reviewText, Double rating, Long userId) throws CommonException {
        Review review = this.getReviewById(reviewId);

        if(review.getUser().getId().equals(userId)) {
            review.setReviewText(reviewText);
            review.setRating(rating);
            return reviewRepository.save(review);
        }
        throw new CommonException(messageMultiUtils.getMessage("review.update.not.allowed"));
    }
    @Override
    public void deleteReview(Long reviewId, Long userId) throws CommonException {
        Review review = this.getReviewById(reviewId);
        if (!review.getUser().getId().equals(userId)) {
            throw new CommonException(messageMultiUtils.getMessage("review.delete.not.allowed"));
        }
        reviewRepository.delete(review);
    }

    @Override
    public Review getReviewById(Long reviewId) throws CommonException {
        return reviewRepository.findById(reviewId).orElseThrow(() -> new CommonException(messageMultiUtils.getMessage("review.not.found")));
    }
}
