package com.shop.ecommerce.service;

import com.shop.ecommerce.exception.CommonException;
import com.shop.ecommerce.modal.Cart;
import com.shop.ecommerce.modal.Coupon;
import com.shop.ecommerce.modal.User;

import java.util.List;

public interface CouponService {
    Cart applyCoupon(String code, Double orderValue, User user) throws CommonException;
    Cart removeCoupon(String code, User user) throws CommonException;
    Coupon findCouponById(Long id) throws CommonException;
    Coupon createCoupon(Coupon coupon);
    List<Coupon> findAllCoupons();
    void deleteCoupon(Long id) throws CommonException;
}
