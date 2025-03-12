package com.shop.ecommerce.service.impl;

import com.razorpay.Payment;
import com.razorpay.PaymentLink;
import com.razorpay.RazorpayClient;
import com.razorpay.RazorpayException;
import com.shop.ecommerce.domain.PaymentMethod;
import com.shop.ecommerce.domain.PaymentOrderStatus;
import com.shop.ecommerce.domain.PaymentStatus;
import com.shop.ecommerce.exception.CommonException;
import com.shop.ecommerce.modal.Order;
import com.shop.ecommerce.modal.PaymentOrder;
import com.shop.ecommerce.modal.User;
import com.shop.ecommerce.repository.OrderRepository;
import com.shop.ecommerce.repository.PaymentOrderRepository;
import com.shop.ecommerce.service.PaymentService;
import com.shop.ecommerce.utils.MessageMultiUtils;
import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.checkout.Session;
import com.stripe.param.checkout.SessionCreateParams;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.json.JSONObject;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;
@Service
@RequiredArgsConstructor
public class PaymentServiceImpl implements PaymentService {
    private final PaymentOrderRepository paymentOrderRepository;
    private final OrderRepository orderRepository;
    private final MessageMultiUtils messageMultiUtils;
    private final String apiKey = "rzp_test_158VYOC0Zz6Dy5";
    private final String apiSecret = "WNS0aeRa5iQphSGmVQUUHVHq";
    private final String stripeSecretKey = "sk_test_51PSJNc06kNtX7JtG7PscgRF0RzHU28URPMtEvD1Ci9Zqs4gFm6jm8Rgp4q37TRKOuCOkbVELrGDZAo4WF6hfEal200W0OtileG";
    @Override
    public PaymentOrder createOrder(User user, Set<Order> orders, PaymentMethod paymentMethod) {
        Long amount = orders.stream().mapToLong(Order::getTotalSellingPrice).sum();
        PaymentOrder paymentOrder = PaymentOrder.builder().status(PaymentOrderStatus.PENDING)
                .status(PaymentOrderStatus.PENDING)
                .paymentMethod(paymentMethod).amount(amount).user(user).orders(orders).build();
        return paymentOrderRepository.save(paymentOrder);
    }

    @Override
    public PaymentOrder getPaymentOrderById(Long orderId) throws CommonException {
        return paymentOrderRepository.findById(orderId).orElseThrow(() -> new CommonException(messageMultiUtils.getMessage("payment.order.not.found")));
    }

    @Override
    public List<PaymentOrder> getPaymentOrderByUserId(Long userId) throws CommonException {
        List<PaymentOrder> paymentOrder = paymentOrderRepository.findByUserId(userId);
        if (paymentOrder == null) {
            throw new CommonException(messageMultiUtils.getMessage("payment.order.not.found"));
        }
        return paymentOrder;
    }

    @Override
    public PaymentOrder getPaymentOrderByPaymentId(String paymentId) throws CommonException {
        PaymentOrder paymentOrder = paymentOrderRepository.findByPaymentLinkId(paymentId);
        if (paymentOrder == null) {
            throw new CommonException(messageMultiUtils.getMessage("payment.order.not.found"));
        }
        return paymentOrder;
    }

    @Override
    public Boolean proceedPaymentOrder(PaymentOrder paymentOrder, String paymentId, String paymentLinkId) throws RazorpayException, StripeException {
        if (paymentOrder.getPaymentMethod().equals(PaymentMethod.RAZORPAY)){
            if (paymentOrder.getStatus().equals(PaymentOrderStatus.PENDING)) {
                RazorpayClient razorpayClient = new RazorpayClient(apiKey, apiSecret);

                Payment payment = razorpayClient.payments.fetch(paymentId);
                String status = payment.get("status");
                if (status.equals("captured")) {
                    Set<Order> order = paymentOrder.getOrders();
                    for (Order o : order) {
                        o.setPaymentStatus(PaymentStatus.COMPLETED);
                        orderRepository.save(o);
                    }
                    paymentOrder.setStatus(PaymentOrderStatus.SUCCESS);
                    paymentOrderRepository.save(paymentOrder);
                    return true;
                }
                paymentOrder.setStatus(PaymentOrderStatus.FAILED);
                paymentOrderRepository.save(paymentOrder);
                return false;
            }
        } else if (paymentOrder.getPaymentMethod().equals(PaymentMethod.STRIPE)) {
            if (paymentOrder.getStatus().equals(PaymentOrderStatus.PENDING)) {
                Stripe.apiKey = stripeSecretKey;
                Session session = Session.retrieve(paymentId);
                if (session.getPaymentStatus().equals("paid")) {
                    Set<Order> order = paymentOrder.getOrders();
                    for (Order o : order) {
                        o.setPaymentStatus(PaymentStatus.COMPLETED);
                        orderRepository.save(o);
                    }
                    paymentOrder.setStatus(PaymentOrderStatus.SUCCESS);
                    paymentOrderRepository.save(paymentOrder);
                    return true;
                }
                paymentOrder.setStatus(PaymentOrderStatus.FAILED);
                paymentOrderRepository.save(paymentOrder);
                return false;
            }
        }
        return false;
    }

    @Override
    public PaymentLink createRazorpayPayment(User user, Long amount, Long orderId) throws RazorpayException {
        try {
            RazorpayClient razorpayClient = new RazorpayClient(apiKey, apiSecret);

            JSONObject paymentLinkReq = getJsonObject(user, amount, orderId);

            PaymentLink paymentLink =  razorpayClient.paymentLink.create(paymentLinkReq);
            String paymentLinkUrl = paymentLink.get("short_url");
            String paymentLinkId = paymentLink.get("id");

            return paymentLink;
        } catch (RazorpayException e) {
            throw new RazorpayException(e.getMessage());
        }
    }

    @NotNull
    private static JSONObject getJsonObject(User user, Long amount, Long orderId) {
        JSONObject paymentLinkReq = new JSONObject();
        paymentLinkReq.put("amount", amount);
        paymentLinkReq.put("currency", "VND");

        JSONObject customer = new JSONObject();
        customer.put("name", user.getFullName());
        customer.put("email", user.getEmail());
        paymentLinkReq.put("customer", customer);

        JSONObject notify = new JSONObject();
        notify.put("email", true);
        paymentLinkReq.put("notify", notify);

        paymentLinkReq.put("callback_url", "http://localhost:3000/payment-success/" + orderId);
        paymentLinkReq.put("callback_method", "get");
        return paymentLinkReq;
    }

    @Override
    public String createStripePaymentLink(User user, Long amount, Long orderId) throws StripeException {
        Stripe.apiKey = stripeSecretKey;

        SessionCreateParams params = SessionCreateParams.builder()
                .addPaymentMethodType(SessionCreateParams.PaymentMethodType.CARD)
                .setMode(SessionCreateParams.Mode.PAYMENT)
                .setSuccessUrl("http://localhost:3000/payment-success/" + orderId)
                .setCancelUrl("http://localhost:3000/payment-cancel")
                .addLineItem(SessionCreateParams.LineItem.builder()
                                .setQuantity(1L)
                                .setPriceData(SessionCreateParams.LineItem.PriceData.builder()
                                                .setCurrency("vnd")
                                                .setUnitAmount(amount)
                                                .setProductData(
                                                        SessionCreateParams.LineItem.PriceData.ProductData.builder()
                                                                .setName("Tuan Anh Payment")
                                                                .build()
                                                ).build()
                                ).build()
                ).build();
        Session session = Session.create(params);
        return session.getUrl();
    }
}
