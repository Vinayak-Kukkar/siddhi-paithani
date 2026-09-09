package com.siddhi.paithani.service;

import com.siddhi.paithani.dto.CartItem;
import com.siddhi.paithani.entity.Order;
import java.util.List;

public interface OrderService {
    Order createOrder(Order order, List<CartItem> cartItems);
    Order getOrderById(Long id);
    Order getOrderByOrderNumber(String orderNumber);
    List<Order> getAllOrders();
    List<Order> getOrdersByCustomerSearch(String search);
    Order updateOrderStatus(Long orderId, String status);
    Order updateOrderTracking(Long orderId, String status, String courierName, String trackingNumber);
}
