package com.siddhi.paithani.service.impl;

import com.siddhi.paithani.dto.CartItem;
import com.siddhi.paithani.entity.Order;
import com.siddhi.paithani.entity.OrderItem;
import com.siddhi.paithani.entity.Product;
import com.siddhi.paithani.repository.OrderRepository;
import com.siddhi.paithani.repository.ProductRepository;
import com.siddhi.paithani.service.NotificationDispatcherService;
import com.siddhi.paithani.service.NotificationService;
import com.siddhi.paithani.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;
    private final NotificationService notificationService;
    private final NotificationDispatcherService notificationDispatcherService;

    @Autowired
    public OrderServiceImpl(OrderRepository orderRepository,
                             ProductRepository productRepository,
                             NotificationService notificationService,
                             NotificationDispatcherService notificationDispatcherService) {
        this.orderRepository = orderRepository;
        this.productRepository = productRepository;
        this.notificationService = notificationService;
        this.notificationDispatcherService = notificationDispatcherService;
    }

    @Override
    @Transactional
    public Order createOrder(Order order, List<CartItem> cartItems) {
        if (cartItems == null || cartItems.isEmpty()) {
            throw new IllegalArgumentException("Cannot place order with an empty cart");
        }

        // Generate unique order number
        String orderNumber = "SP-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        order.setOrderNumber(orderNumber);
        order.setCreatedAt(LocalDateTime.now());
        order.setStatus("CONFIRMED");

        double totalAmount = 0.0;

        for (CartItem item : cartItems) {
            Product product = item.getProduct();
            if (product == null) continue;

            // Reduce stock
            if (product.getStock() != null && product.getStock() >= item.getQuantity()) {
                int newStock = product.getStock() - item.getQuantity();
                product.setStock(newStock);
                productRepository.save(product);

                // Automated Low Stock Alert Trigger to Master Weaver
                if (newStock <= 3 && notificationService != null) {
                    try {
                        notificationService.sendLowStockAlert(product);
                    } catch (Exception ignored) {}
                }
            }


            OrderItem orderItem = new OrderItem(product, item.getQuantity(), product.getPrice());
            order.addItem(orderItem);
            totalAmount += orderItem.getSubtotal();
        }

        double discount = (order.getDiscountAmount() != null) ? order.getDiscountAmount() : 0.0;
        double giftFee = (order.getGiftWrap() != null && order.getGiftWrap()) ? 150.0 : 0.0;
        double finalPayable = Math.max(0.0, totalAmount - discount + giftFee);
        order.setTotalAmount(finalPayable);
        Order savedOrder = orderRepository.save(order);

        // Send Email & SMS Notification to Customer
        try {
            if (notificationService != null) {
                notificationService.sendOrderConfirmationNotification(savedOrder);
            }
        } catch (Exception e) {
            System.err.println("Failed to send order notification: " + e.getMessage());
        }

        return savedOrder;
    }

    @Override
    public Order getOrderById(Long id) {
        return orderRepository.findById(id).orElse(null);
    }

    @Override
    public Order getOrderByOrderNumber(String orderNumber) {
        return orderRepository.findByOrderNumber(orderNumber).orElse(null);
    }

    @Override
    public List<Order> getAllOrders() {
        return orderRepository.findAllByOrderByCreatedAtDesc();
    }

    @Override
    public List<Order> getOrdersByCustomerSearch(String search) {
        if (search == null || search.trim().isEmpty()) {
            return List.of();
        }
        String cleanSearch = search.trim();
        if (cleanSearch.startsWith("SP-") || cleanSearch.startsWith("sp-")) {
            Order order = getOrderByOrderNumber(cleanSearch.toUpperCase());
            return order != null ? List.of(order) : List.of();
        }
        return orderRepository.findByEmailIgnoreCaseOrMobileOrderByCreatedAtDesc(cleanSearch, cleanSearch);
    }

    @Override
    public Order updateOrderStatus(Long orderId, String status) {
        Order order = getOrderById(orderId);
        if (order != null) {
            order.setStatus(status);
            Order saved = orderRepository.save(order);
            notificationDispatcherService.sendSmsNotification(saved);
            return saved;
        }
        return null;
    }

    @Override
    public Order updateOrderTracking(Long orderId, String status, String courierName, String trackingNumber) {
        Order order = getOrderById(orderId);
        if (order != null) {
            if (status != null && !status.trim().isEmpty()) {
                order.setStatus(status);
            }
            if (courierName != null && !courierName.trim().isEmpty()) {
                order.setCourierName(courierName);
            }
            if (trackingNumber != null && !trackingNumber.trim().isEmpty()) {
                order.setTrackingNumber(trackingNumber);
            }
            Order saved = orderRepository.save(order);
            notificationDispatcherService.sendSmsNotification(saved);
            return saved;
        }
        return null;
    }
}
