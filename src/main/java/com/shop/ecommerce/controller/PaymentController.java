package com.shop.ecommerce.controller;

import com.razorpay.RazorpayException;
import com.shop.ecommerce.exception.CartException;
import com.shop.ecommerce.exception.CommonException;
import com.shop.ecommerce.exception.SellerException;
import com.shop.ecommerce.modal.*;
import com.shop.ecommerce.response.ApiResponse;
import com.shop.ecommerce.response.PaymentLinkResponse;
import com.shop.ecommerce.service.*;
import com.shop.ecommerce.utils.JWT_CONSTANT;
import com.shop.ecommerce.utils.MessageMultiUtils;
import com.stripe.exception.StripeException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/payment")
public class PaymentController {

    private final PaymentService paymentService;
    private final UserService userService;
    private final SellerReportService sellerReportService;
    private final SellerService sellerService;
    private final MessageMultiUtils messageMultiUtils;
    private final TransactionService transactionService;
    private final CartItemService cartItemServicer;
    private final CartService cartService;

    @GetMapping("/{paymentId}")
    public ResponseEntity<Object> paymentSuccessHandle(@PathVariable String paymentId, @RequestParam String paymentLinkId, @RequestHeader(JWT_CONSTANT.JWT_HEADER) String token) throws CommonException, RazorpayException, SellerException, StripeException, CartException {
        User user = userService.findUserByJwtToken(token);

        PaymentLinkResponse paymentLinkResponse;
        PaymentOrder paymentOrder = paymentService.getPaymentOrderByPaymentId(paymentLinkId);

        Boolean paymentSuccess = paymentService.proceedPaymentOrder(paymentOrder, paymentId, paymentLinkId);
        if (paymentSuccess) {
            for (Order order : paymentOrder.getOrders()) {
               transactionService.createTransaction(order);
               Seller seller = sellerService.getSellerById(order.getSellerId());
               SellerReport sellerReport = sellerReportService.getSellerReport(seller);
               sellerReport.setTotalOrders(sellerReport.getTotalOrders() + 1);
               sellerReport.setTotalEarnings(sellerReport.getTotalEarnings() + order.getTotalSellingPrice());
               sellerReport.setTotalSales(sellerReport.getTotalSales() + order.getOrderItems().size());
               sellerReportService.updateSellerReport(sellerReport);
            }
            Cart cart = cartService.findUserCart(user);
            for (CartItem cartItem : cart.getCartItems()) {
                cartService.deleteCartItem(user, cartItem.getId());
            }
         }
        ApiResponse apiResponse = new ApiResponse();
        apiResponse.setMessage(messageMultiUtils.getMessage("payment.success"));
        return ResponseEntity.ok(apiResponse);
    }
}
