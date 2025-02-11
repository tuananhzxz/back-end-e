package com.shop.ecommerce.controller;

import com.shop.ecommerce.exception.CommonException;
import com.shop.ecommerce.modal.Cart;
import com.shop.ecommerce.modal.Coupon;
import com.shop.ecommerce.modal.User;
import com.shop.ecommerce.service.CartService;
import com.shop.ecommerce.service.CouponService;
import com.shop.ecommerce.service.UserService;
import com.shop.ecommerce.utils.JWT_CONSTANT;
import com.shop.ecommerce.utils.MessageMultiUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/admin/coupon")
public class AdminCouponController {

    private final CouponService couponService;
    private final UserService userService;
    private final MessageMultiUtils messageMultiUtils;

    @PostMapping("/apply")
    public ResponseEntity<Object> applyCoupon(@RequestParam String apply, @RequestParam String code, @RequestParam Double orderValue, @RequestHeader(JWT_CONSTANT.JWT_HEADER) String token) throws CommonException {
        User user = userService.findUserByJwtToken(token);
        Cart cart;
        if (apply.equals("true")) cart = couponService.applyCoupon(code, orderValue, user);
        else cart = couponService.removeCoupon(code, user);
        return ResponseEntity.ok(cart);
    }

    @PostMapping("/admin/create")
    public ResponseEntity<Object> createCoupon(@RequestBody Coupon coupon) throws CommonException {
        Coupon newCoupon = couponService.createCoupon(coupon);
        return ResponseEntity.ok(newCoupon);
    }

    @DeleteMapping("/admin/delete/{id}")
    public ResponseEntity<Object> deleteCoupon(@PathVariable Long id) throws CommonException {
        couponService.deleteCoupon(id);
        return ResponseEntity.ok(messageMultiUtils.getMessage("coupon.delete.success"));
    }

    @GetMapping("/admin/all")
    public ResponseEntity<List<?>> findAllCoupons() {
        List<Coupon> coupons = couponService.findAllCoupons();
        return ResponseEntity.ok(coupons);
    }
}
