package com.shop.ecommerce.service.impl;

import com.shop.ecommerce.exception.CommonException;
import com.shop.ecommerce.modal.Cart;
import com.shop.ecommerce.modal.Coupon;
import com.shop.ecommerce.modal.User;
import com.shop.ecommerce.repository.CartRepository;
import com.shop.ecommerce.repository.CouponRepository;
import com.shop.ecommerce.repository.UserRepository;
import com.shop.ecommerce.service.CouponService;
import com.shop.ecommerce.utils.MessageMultiUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CouponServiceImpl implements CouponService {

    private final CouponRepository couponRepository;
    private final CartRepository cartRepository;
    private final UserRepository userRepository;
    private final MessageMultiUtils messageMultiUtils;
    @Override
    public Cart applyCoupon(String code, Double orderValue, User user) throws CommonException {
        Coupon coupon = couponRepository.findByCode(code);
        Cart cart = cartRepository.findByUserId(user.getId());

        if (coupon == null) throw new CommonException(messageMultiUtils.getMessage("coupon.not.found"));
        if (user.getUsedCoupons().contains(coupon)) throw new CommonException(messageMultiUtils.getMessage("coupon.already.used"));
        if (orderValue < coupon.getMinimumOrderValue()) throw new CommonException(messageMultiUtils.getMessage("coupon.minimum.order.value") + ' ' + coupon.getMinimumOrderValue());
        if(coupon.getValidityEndDate().minusDays(1).isBefore(LocalDate.now())) throw new CommonException(messageMultiUtils.getMessage("coupon.expired"));

        if (coupon.getIsActive() && LocalDate.now().isAfter(coupon.getValidityStartDate()) && LocalDate.now().isBefore(coupon.getValidityEndDate())) {
            user.getUsedCoupons().add(coupon);
            userRepository.save(user);

            Double discountPrice = (cart.getTotalSellingPrice() * coupon.getDiscountPercentage()) / 100;
            cart.setDiscount(discountPrice.intValue());
            cart.setTotalSellingPrice(cart.getTotalSellingPrice() - discountPrice.intValue());
            cart.setCurrentPrice(cart.getTotalSellingPrice());
            cart.setCouponCode(code);
            cartRepository.save(cart);
            return cart;
        }
        throw new CommonException(messageMultiUtils.getMessage("coupon.invalid"));
    }

    @Override
    public Cart removeCoupon(String code, User user) throws CommonException {
        Coupon coupon = couponRepository.findByCode(code);
        if (coupon == null) throw new CommonException(messageMultiUtils.getMessage("coupon.not.found"));
        Cart cart = cartRepository.findByUserId(user.getId());
        Double discountPrice = ( cart.getTotalSellingPrice() * coupon.getDiscountPercentage() ) / 100;
        cart.setTotalSellingPrice((int) (cart.getTotalSellingPrice() + discountPrice));
        cart.setCouponCode(null);
        return cartRepository.save(cart);
    }

    @Override
    public Coupon findCouponById(Long id) throws CommonException {
        return couponRepository.findById(id).orElseThrow(() -> new CommonException(messageMultiUtils.getMessage("coupon.not.found")));
    }

    @Override
    @PreAuthorize("hasRole('Admin')")
    public Coupon createCoupon(Coupon coupon) {
        return couponRepository.save(coupon);
    }

    @Override
    public List<Coupon> findAllCoupons() {
        return couponRepository.findAll();
    }

    @Override
    @PreAuthorize("hasRole('Admin')")
    public void deleteCoupon(Long id) throws CommonException {
        this.findCouponById(id);
        couponRepository.deleteById(id);
    }
}
