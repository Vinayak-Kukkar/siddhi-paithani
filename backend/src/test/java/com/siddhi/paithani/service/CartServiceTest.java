package com.siddhi.paithani.service;

import com.siddhi.paithani.dto.CartItem;
import com.siddhi.paithani.entity.Product;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class CartServiceTest {

    private CartService cartService;
    private Product p1;
    private Product p2;

    @BeforeEach
    void setUp() {
        cartService = new CartService();

        p1 = new Product();
        p1.setId(101L);
        p1.setName("Kanjivaram Silk Saree");
        p1.setPrice(12000.0);

        p2 = new Product();
        p2.setId(102L);
        p2.setName("Peshwai Paithani Saree");
        p2.setPrice(25000.0);
    }

    @Test
    void testAddItem_NewProducts() {
        cartService.addItem(p1, 1);
        cartService.addItem(p2, 2);

        assertEquals(3, cartService.getItemCount());
        assertEquals(62000.0, cartService.getTotalAmount()); // (12000 * 1) + (25000 * 2)

        List<CartItem> items = cartService.getItems();
        assertEquals(2, items.size());
    }

    @Test
    void testAddItem_ExistingProduct_IncrementsQuantity() {
        cartService.addItem(p1, 1);
        cartService.addItem(p1, 3);

        assertEquals(4, cartService.getItemCount());
        assertEquals(48000.0, cartService.getTotalAmount()); // 12000 * 4
    }

    @Test
    void testUpdateQuantity() {
        cartService.addItem(p1, 1);
        cartService.updateQuantity(101L, 5);

        assertEquals(5, cartService.getItemCount());
        assertEquals(60000.0, cartService.getTotalAmount());
    }

    @Test
    void testRemoveItem() {
        cartService.addItem(p1, 2);
        cartService.addItem(p2, 1);

        cartService.removeItem(101L);

        assertEquals(1, cartService.getItemCount());
        assertEquals(25000.0, cartService.getTotalAmount());
    }

    @Test
    void testClearCart() {
        cartService.addItem(p1, 2);
        cartService.addItem(p2, 3);

        cartService.clearCart();

        assertEquals(0, cartService.getItemCount());
        assertEquals(0.0, cartService.getTotalAmount());
        assertTrue(cartService.getItems().isEmpty());
    }
}
