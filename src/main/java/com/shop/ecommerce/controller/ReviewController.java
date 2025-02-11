package com.shop.ecommerce.controller;

import com.shop.ecommerce.exception.CommonException;
import com.shop.ecommerce.modal.Product;
import com.shop.ecommerce.modal.Review;
import com.shop.ecommerce.modal.User;
import com.shop.ecommerce.request.CreateReviewRequest;
import com.shop.ecommerce.response.ApiResponse;
import com.shop.ecommerce.service.ProductService;
import com.shop.ecommerce.service.ReviewService;
import com.shop.ecommerce.service.UserService;
import com.shop.ecommerce.utils.JWT_CONSTANT;
import com.shop.ecommerce.utils.MessageMultiUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/review")
public class ReviewController {

    private final ReviewService reviewService;
    private final UserService userService;
    private final ProductService productService;
    private final MessageMultiUtils messageMultiUtils;

    @GetMapping("/product/{productId}/reviews")
    public ResponseEntity<List<?>> getReviewsByProductId(@PathVariable Long productId) {
        return ResponseEntity.ok(reviewService.getReviewsByProductId(productId));
    }

    @PostMapping("/product/{productId}/reviews")
    public ResponseEntity<Object> writeReview(@RequestBody CreateReviewRequest request, @PathVariable Long productId, @RequestHeader(JWT_CONSTANT.JWT_HEADER) String token) throws CommonException {
        User user = userService.findUserByJwtToken(token);
        Product product = productService.findProductById(productId);
        Review review = reviewService.createReview(request, user, product);
        return ResponseEntity.ok(review);
    }

    @PatchMapping("/review/{reviewId}")
    public ResponseEntity<Object> updateReview(@PathVariable Long reviewId, @RequestBody CreateReviewRequest request, @RequestHeader(JWT_CONSTANT.JWT_HEADER) String token) throws CommonException {
        User user = userService.findUserByJwtToken(token);
        Review review = reviewService.updateReview(reviewId, request.getReviewText(), request.getReviewRating(), user.getId());
        return ResponseEntity.ok(review);
    }

    @DeleteMapping("/review/{reviewId}")
    public ResponseEntity<Object> deleteReview(@PathVariable Long reviewId, @RequestHeader(JWT_CONSTANT.JWT_HEADER) String token) throws CommonException {
        User user = userService.findUserByJwtToken(token);
        reviewService.deleteReview(reviewId, user.getId());
        ApiResponse apiResponse = new ApiResponse();
        apiResponse.setMessage(messageMultiUtils.getMessage("review.delete.success"));
        return ResponseEntity.ok(apiResponse);
    }
}
