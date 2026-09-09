package com.siddhi.paithani.dto;

import com.siddhi.paithani.entity.Product;
import java.io.Serializable;

public class WishlistItem implements Serializable {

    private static final long serialVersionUID = 1L;

    private Product product;
    private Double savedPrice;
    private boolean priceDropped;
    private Double priceDropAmount = 0.0;
    private boolean backInStock;
    private boolean alertEnabled = true;

    public WishlistItem() {}

    public WishlistItem(Product product) {
        this.product = product;
        this.savedPrice = product != null ? product.getPrice() : 0.0;
        checkAlerts();
    }

    public void checkAlerts() {
        if (product != null && savedPrice != null && product.getPrice() != null) {
            if (product.getPrice() < savedPrice) {
                this.priceDropped = true;
                this.priceDropAmount = savedPrice - product.getPrice();
            } else {
                this.priceDropped = false;
                this.priceDropAmount = 0.0;
            }
            if (product.getStock() != null && product.getStock() > 0) {
                this.backInStock = true;
            } else {
                this.backInStock = false;
            }
        }
    }

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }

    public Double getSavedPrice() {
        return savedPrice;
    }

    public void setSavedPrice(Double savedPrice) {
        this.savedPrice = savedPrice;
    }

    public boolean isPriceDropped() {
        return priceDropped;
    }

    public void setPriceDropped(boolean priceDropped) {
        this.priceDropped = priceDropped;
    }

    public Double getPriceDropAmount() {
        return priceDropAmount;
    }

    public void setPriceDropAmount(Double priceDropAmount) {
        this.priceDropAmount = priceDropAmount;
    }

    public boolean isBackInStock() {
        return backInStock;
    }

    public void setBackInStock(boolean backInStock) {
        this.backInStock = backInStock;
    }

    public boolean isAlertEnabled() {
        return alertEnabled;
    }

    public void setAlertEnabled(boolean alertEnabled) {
        this.alertEnabled = alertEnabled;
    }
}
