package com.shop.ecommerce.exception;

import com.shop.ecommerce.modal.Product;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;

import java.time.LocalDateTime;

@ControllerAdvice
public class GlobalException {

    @ExceptionHandler(SellerException.class)
    public ResponseEntity<Object> handleSellerException(SellerException e, WebRequest req) {
        ErrorDetails errorDetails = new ErrorDetails(e.getMessage(), req.getDescription(false), LocalDateTime.now());
        return ResponseEntity.badRequest().body(errorDetails);
    }

    @ExceptionHandler(CommonException.class)
    public ResponseEntity<Object> handleProductException(CommonException e, WebRequest req) {
        ErrorDetails errorDetails = new ErrorDetails(e.getMessage(), req.getDescription(false), LocalDateTime.now());
        return ResponseEntity.badRequest().body(errorDetails);
    }

    @ExceptionHandler(CartException.class)
    public ResponseEntity<Object> handleCartException(CartException e, WebRequest req) {
        ErrorDetails errorDetails = new ErrorDetails(e.getMessage(), req.getDescription(false), LocalDateTime.now());
        return ResponseEntity.badRequest().body(errorDetails);
    }
}
