package com.siddhi.paithani.dto;

import com.siddhi.paithani.entity.Product;

public class CartItem {
    private Product product;
    private int quantity;

    public CartItem() {
    }

    public CartItem(Product product, int quantity) {
        this.product = product;
        this.quantity = quantity;
    }

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public double getSubtotal() {
        if (product != null && product.getPrice() != null) {
            return product.getPrice() * quantity;
        }
        return 0.0;
    }

    // Property getters accessed in cart and checkout templates
    public Long getProductId() {
        return product != null ? product.getId() : null;
    }

    public String getProductName() {
        return product != null ? product.getName() : "";
    }

    public String getImageUrl() {
        return product != null ? product.getImageUrl() : "";
    }

    public Double getPrice() {
        return product != null ? product.getPrice() : 0.0;
    }
}
