package com.siddhi.paithani.service;

import com.siddhi.paithani.dto.CartItem;
import com.siddhi.paithani.entity.Order;
import com.siddhi.paithani.entity.Product;
import com.siddhi.paithani.repository.OrderRepository;
import com.siddhi.paithani.repository.ProductRepository;
import com.siddhi.paithani.service.impl.OrderServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrderServiceImplTest {

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private ProductRepository productRepository;

    @Mock
    private NotificationService notificationService;

    @Mock
    private NotificationDispatcherService notificationDispatcherService;

    @InjectMocks
    private OrderServiceImpl orderService;

    private Product testProduct;
    private Order testOrder;

    @BeforeEach
    void setUp() {
        testProduct = new Product();
        testProduct.setId(1L);
        testProduct.setName("Yeola Paithani Saree");
        testProduct.setPrice(15000.0);
        testProduct.setStock(10);

        testOrder = new Order();
        testOrder.setCustomerName("Anvita Sharma");
        testOrder.setEmail("anvita@example.com");
        testOrder.setMobile("9876543210");
    }

    @Test
    void testCreateOrder_Success() {
        CartItem cartItem = new CartItem(testProduct, 2);
        List<CartItem> cartItems = List.of(cartItem);

        when(orderRepository.save(any(Order.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Order createdOrder = orderService.createOrder(testOrder, cartItems);

        assertNotNull(createdOrder);
        assertNotNull(createdOrder.getOrderNumber());
        assertTrue(createdOrder.getOrderNumber().startsWith("SP-"));
        assertEquals("CONFIRMED", createdOrder.getStatus());
        assertEquals(30000.0, createdOrder.getTotalAmount());
        assertEquals(8, testProduct.getStock()); // 10 - 2

        verify(productRepository, times(1)).save(testProduct);
        verify(orderRepository, times(1)).save(any(Order.class));
    }

    @Test
    void testCreateOrder_EmptyCart_ThrowsException() {
        assertThrows(IllegalArgumentException.class, () -> {
            orderService.createOrder(testOrder, Collections.emptyList());
        });

        verifyNoInteractions(orderRepository);
    }

    @Test
    void testUpdateOrderStatus_Success() {
        testOrder.setId(100L);
        testOrder.setStatus("CONFIRMED");

        when(orderRepository.findById(100L)).thenReturn(Optional.of(testOrder));
        when(orderRepository.save(any(Order.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Order updated = orderService.updateOrderStatus(100L, "DISPATCHED");

        assertNotNull(updated);
        assertEquals("DISPATCHED", updated.getStatus());
        verify(notificationDispatcherService, times(1)).sendSmsNotification(testOrder);
    }
}
