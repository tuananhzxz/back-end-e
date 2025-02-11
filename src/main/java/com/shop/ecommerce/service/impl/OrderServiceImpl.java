package com.shop.ecommerce.service.impl;

import com.shop.ecommerce.domain.OrderStatus;
import com.shop.ecommerce.domain.PaymentStatus;
import com.shop.ecommerce.exception.CommonException;
import com.shop.ecommerce.modal.*;
import com.shop.ecommerce.repository.AddressRepository;
import com.shop.ecommerce.repository.OrderItemRepository;
import com.shop.ecommerce.repository.OrderRepository;
import com.shop.ecommerce.service.OrderService;
import com.shop.ecommerce.utils.MessageMultiUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final AddressRepository addressRepository;
    private final OrderItemRepository orderItemRepository;
    private final MessageMultiUtils messageMultiUtils;

//    @Override
//    public Set<Order> createOrder(User user, Address shipAddress, Cart cart) {
//        Address address = this.saveShippingAddress(user, shipAddress);
//        Map<Long, List<CartItem>> itemBySeller = this.groupItemsBySeller(cart);
//
//        Set<Order> orders = new HashSet<>();
//        for (Map.Entry<Long, List<CartItem>> entry : itemBySeller.entrySet()) {
//            Long sellerId = entry.getKey();
//            List<CartItem> items = entry.getValue();
//
//            Order savedOrder = this.createAndSaveOrder(user, address, sellerId, items);
//            List<OrderItem> orderItems = this.createAndSaveOrderItems(savedOrder, items);
//            savedOrder.setOrderItems(orderItems);
//
//            orders.add(savedOrder);
//        }
//        return orders;
//    }
    @Override
    public Set<Order> createOrder(User user, Address shipAddress, Cart cart) {
    Address address = user.getAddresses().stream()
            .filter(addr -> addr.equals(shipAddress))
            .findFirst()
            .orElseGet(() -> {
                user.getAddresses().add(shipAddress);
                return addressRepository.save(shipAddress);
            });

    Map<Long, List<CartItem>> itemBySeller = cart.getCartItems().stream()
            .collect(Collectors.groupingBy(cartItem -> cartItem.getProduct().getSeller().getId()));

    Set<Order> orders = new HashSet<>();
    for (Map.Entry<Long, List<CartItem>> entry : itemBySeller.entrySet()) {
        Long sellerId = entry.getKey();
        List<CartItem> items = entry.getValue();

        int totalOrderPrice = items.stream().mapToInt(CartItem::getSellingPrice).sum();
        int totalItem = items.stream().mapToInt(CartItem::getQuantity).sum();

        Order createdOrder = new Order();
        createdOrder.setUser(user);
        createdOrder.setSellerId(sellerId);
        createdOrder.setTotalMrpPrice(totalOrderPrice * 1.0);
        createdOrder.setTotalSellingPrice(totalOrderPrice);
        createdOrder.setTotalItem(totalItem);
        createdOrder.setShippingAddress(address);
        createdOrder.setOrderStatus(OrderStatus.PENDING);
        createdOrder.getPaymentDetails().setStatus(PaymentStatus.PENDING);

        Order savedOrder = orderRepository.save(createdOrder);
        orders.add(savedOrder);

        List<OrderItem> orderItems = new ArrayList<>();
        for (CartItem item : items) {
            OrderItem orderItem = new OrderItem();
            orderItem.setOrder(savedOrder);
            orderItem.setMrpPrice(item.getMrpPrice() * 1.0);
            orderItem.setProduct(item.getProduct());
            orderItem.setQuantity(item.getQuantity());
            orderItem.setSize(item.getSize());
            orderItem.setUserId(item.getUserId());
            orderItem.setSellingPrice(item.getSellingPrice());

            savedOrder.getOrderItems().add(orderItem);

            OrderItem savedOrderItem = orderItemRepository.save(orderItem);
            orderItems.add(savedOrderItem);
        }
    }
    return orders;
}
    @Override
    public Order findOrderById(Long id) throws CommonException {
        return orderRepository.findById(id).orElseThrow(() -> new CommonException(messageMultiUtils.getMessage("order.not.found")));
    }

    @Override
    public List<Order> usersOrderHistory(Long userId) {
        return orderRepository.findByUserId(userId);
    }

    @Override
    public List<Order> sellersOrder(Long sellerId) {
        return orderRepository.findBySellerId(sellerId);
    }

    @Override
    public Order updateOrderStatus(Long orderId, OrderStatus status) throws CommonException {
        Order order = this.findOrderById(orderId);
        order.setOrderStatus(status);
        return orderRepository.save(order);
    }

    @Override
    public Order cancelOrder(Long orderId, User user) throws CommonException {
        Order order = this.findOrderById(orderId);
        if (!order.getUser().equals(user)) {
            throw new CommonException(messageMultiUtils.getMessage("order.not.found"));
        }
        order.setOrderStatus(OrderStatus.CANCELLED);
        return orderRepository.save(order);
    }

    @Override
    public OrderItem getOrderItemById(Long id) throws CommonException {
        return orderItemRepository.findById(id).orElseThrow(() -> new CommonException(messageMultiUtils.getMessage("order.item.not.found")));
    }

    private Address saveShippingAddress(User user, Address shipAddress) {
        if (!user.getAddresses().contains(shipAddress)) {
            user.getAddresses().add(shipAddress);
        }
        return addressRepository.save(shipAddress);
    }

    private Map<Long, List<CartItem>> groupItemsBySeller(Cart cart) {
        return cart.getCartItems().stream()
                .collect(Collectors.groupingBy(cartItem -> cartItem.getProduct().getSeller().getId()));
    }

    private Order createAndSaveOrder(User user, Address address, Long sellerId, List<CartItem> items) {
        int totalOrderItems = items.stream().mapToInt(CartItem::getQuantity).sum();
        int totalOrderPrice = items.stream().mapToInt(CartItem::getSellingPrice).sum();

        Order createdOrder = Order.builder()
                .user(user)
                .shippingAddress(address)
                .sellerId(sellerId)
                .totalItem(totalOrderItems)
                .totalMrpPrice(totalOrderPrice * 1.0)
                .totalSellingPrice(totalOrderPrice)
                .orderStatus(OrderStatus.PENDING)
                .paymentDetails(new PaymentDetails())
                .build();
        createdOrder.getPaymentDetails().setStatus(PaymentStatus.PENDING);
        return orderRepository.save(createdOrder);
    }

    private List<OrderItem> createAndSaveOrderItems(Order order, List<CartItem> items) {
        List<OrderItem> orderItems = new ArrayList<>();
        for (CartItem item : items) {
            OrderItem orderItem = OrderItem.builder()
                    .userId(item.getUserId())
                    .order(order)
                    .product(item.getProduct())
                    .quantity(item.getQuantity())
                    .mrpPrice(item.getMrpPrice() * 1.0)
                    .sellingPrice(item.getSellingPrice())
                    .size(item.getSize())
                    .build();
            OrderItem savedOrderItem = orderItemRepository.save(orderItem);
            orderItems.add(savedOrderItem);
        }
        return orderItems;
    }
}
