package com.siddhi.paithani.service;

import com.siddhi.paithani.dto.WishlistItem;
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
public class WishlistService implements Serializable {

    private static final long serialVersionUID = 1L;

    private final List<WishlistItem> wishlistItems = new ArrayList<>();

    public synchronized boolean toggleWishlist(Product product) {
        if (product == null) return false;
        boolean exists = wishlistItems.stream().anyMatch(item -> item.getProduct().getId().equals(product.getId()));
        if (exists) {
            wishlistItems.removeIf(item -> item.getProduct().getId().equals(product.getId()));
            return false; // Removed
        } else {
            wishlistItems.add(new WishlistItem(product));
            return true; // Added
        }
    }

    public synchronized boolean isWishlisted(Long productId) {
        if (productId == null) return false;
        return wishlistItems.stream().anyMatch(item -> item.getProduct().getId().equals(productId));
    }

    public synchronized List<WishlistItem> getWishlistItems() {
        for (WishlistItem item : wishlistItems) {
            item.checkAlerts();
        }
        return new ArrayList<>(wishlistItems);
    }

    public synchronized List<Product> getWishlistProducts() {
        List<Product> products = new ArrayList<>();
        for (WishlistItem item : wishlistItems) {
            products.add(item.getProduct());
        }
        return products;
    }

    public synchronized int getWishlistCount() {
        return wishlistItems.size();
    }

    public synchronized void removeFromWishlist(Long productId) {
        if (productId != null) {
            wishlistItems.removeIf(item -> item.getProduct().getId().equals(productId));
        }
    }

    public synchronized void togglePriceAlert(Long productId) {
        if (productId != null) {
            for (WishlistItem item : wishlistItems) {
                if (item.getProduct().getId().equals(productId)) {
                    item.setAlertEnabled(!item.isAlertEnabled());
                    break;
                }
            }
        }
    }

    public synchronized void clearWishlist() {
        wishlistItems.clear();
    }
}
