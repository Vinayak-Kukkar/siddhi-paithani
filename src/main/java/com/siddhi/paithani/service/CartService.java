package com.siddhi.paithani.service;

import com.siddhi.paithani.dto.CartItem;
import com.siddhi.paithani.entity.Product;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.stereotype.Service;
import org.springframework.web.context.WebApplicationContext;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Service
@Scope(value = WebApplicationContext.SCOPE_SESSION, proxyMode = ScopedProxyMode.TARGET_CLASS)
public class CartService implements Serializable {

    private static final long serialVersionUID = 1L;

    private final List<CartItem> items = new ArrayList<>();

    public synchronized void addItem(Product product, int quantity) {
        if (product == null) return;
        for (CartItem item : items) {
            if (item.getProduct() != null && item.getProduct().getId().equals(product.getId())) {
                item.setQuantity(item.getQuantity() + quantity);
                return;
            }
        }
        items.add(new CartItem(product, quantity));
    }

    public synchronized void updateQuantity(Long productId, int quantity) {
        if (quantity <= 0) {
            removeItem(productId);
            return;
        }
        for (CartItem item : items) {
            if (item.getProduct() != null && item.getProduct().getId().equals(productId)) {
                item.setQuantity(quantity);
                return;
            }
        }
    }

    public synchronized void removeItem(Long productId) {
        items.removeIf(item -> item.getProduct() != null && item.getProduct().getId().equals(productId));
    }

    public synchronized List<CartItem> getItems() {
        return new ArrayList<>(items);
    }

    public synchronized int getItemCount() {
        return items.stream().mapToInt(CartItem::getQuantity).sum();
    }

    public synchronized double getTotalAmount() {
        return items.stream().mapToDouble(CartItem::getSubtotal).sum();
    }

    public synchronized void clearCart() {
        items.clear();
    }
}
