package com.siddhi.paithani.entity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "orders")
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "order_number", unique = true, nullable = false)
    private String orderNumber;

    @NotBlank(message = "Customer name is required")
    @Column(name = "customer_name", nullable = false)
    private String customerName;

    @NotBlank(message = "Email is required")
    @Email(message = "Invalid email address")
    @Column(name = "email", nullable = false)
    private String email;

    @NotBlank(message = "Mobile number is required")
    @Pattern(regexp = "^[0-9]{10}$", message = "Mobile number must be 10 digits")
    @Column(name = "mobile", nullable = false)
    private String mobile;

    @NotBlank(message = "Delivery address is required")
    @Column(name = "address", nullable = false)
    private String address;

    @NotBlank(message = "City is required")
    @Column(name = "city", nullable = false)
    private String city;

    @NotBlank(message = "Pincode is required")
    @Pattern(regexp = "^[0-9]{6}$", message = "Pincode must be 6 digits")
    @Column(name = "pincode", nullable = false)
    private String pincode;

    @Column(name = "total_amount", nullable = false)
    private Double totalAmount;

    @Column(name = "coupon_code")
    private String couponCode;

    @Column(name = "discount_amount")
    private Double discountAmount = 0.0;

    @Column(name = "gift_wrap")
    private Boolean giftWrap = false;

    @Column(name = "gift_recipient_name")
    private String giftRecipientName;

    @Column(name = "gift_occasion")
    private String giftOccasion;

    @Column(name = "gift_message", length = 1000)
    private String giftMessage;

    @Column(name = "gift_wrap_fee")
    private Double giftWrapFee = 0.0;


    @Column(name = "payment_method")
    private String paymentMethod = "UPI / Online"; // UPI / QR Code, Credit/Debit Card, Net Banking, Cash on Delivery

    @Column(name = "payment_status")
    private String paymentStatus = "COMPLETED"; // COMPLETED, PENDING

    @Column(name = "status")
    private String status = "PENDING"; // PENDING, CONFIRMED, SHIPPED, OUT_FOR_DELIVERY, DELIVERED

    @Column(name = "courier_name")
    private String courierName = "Bluedart Express";

    @Column(name = "tracking_number")
    private String trackingNumber;

    @Column(name = "created_at")
    private LocalDateTime createdAt = LocalDateTime.now();

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OrderItem> items = new ArrayList<>();

    public int getStepIndex() {
        if (status == null) return 1;
        switch (status.toUpperCase()) {
            case "DELIVERED":
                return 5;
            case "OUT_FOR_DELIVERY":
                return 4;
            case "SHIPPED":
                return 3;
            case "IN_PRODUCTION":
            case "CONFIRMED":
            case "PACKED":
                return 2;
            case "PENDING":
            case "ORDER_PLACED":
            default:
                return 1;
        }
    }

    public String getCourierName() {
        return courierName != null ? courierName : "Bluedart Express";
    }

    public void setCourierName(String courierName) {
        this.courierName = courierName;
    }

    public String getTrackingNumber() {
        return trackingNumber != null ? trackingNumber : "BD-" + (id != null ? id + 98402 : "98402");
    }

    public void setTrackingNumber(String trackingNumber) {
        this.trackingNumber = trackingNumber;
    }

    public Order() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getOrderNumber() {
        return orderNumber;
    }

    public void setOrderNumber(String orderNumber) {
        this.orderNumber = orderNumber;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    // Alias methods for customerEmail
    public String getCustomerEmail() {
        return email;
    }

    public void setCustomerEmail(String customerEmail) {
        this.email = customerEmail;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    // Alias methods for customerPhone
    public String getCustomerPhone() {
        return mobile;
    }

    public void setCustomerPhone(String customerPhone) {
        this.mobile = customerPhone;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    // Alias methods for shippingAddress
    public String getShippingAddress() {
        return address;
    }

    public void setShippingAddress(String shippingAddress) {
        this.address = shippingAddress;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getPincode() {
        return pincode;
    }

    public void setPincode(String pincode) {
        this.pincode = pincode;
    }

    public Double getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(Double totalAmount) {
        this.totalAmount = totalAmount;
    }

    public String getCouponCode() {
        return couponCode;
    }

    public void setCouponCode(String couponCode) {
        this.couponCode = couponCode;
    }

    public Double getDiscountAmount() {
        return discountAmount != null ? discountAmount : 0.0;
    }

    public void setDiscountAmount(Double discountAmount) {
        this.discountAmount = discountAmount;
    }

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public String getPaymentStatus() {
        return paymentStatus;
    }

    public void setPaymentStatus(String paymentStatus) {
        this.paymentStatus = paymentStatus;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public List<OrderItem> getItems() {
        return items;
    }

    public void setItems(List<OrderItem> items) {
        this.items = items;
    }

    public List<OrderItem> getOrderItems() {
        return items;
    }

    public Boolean getGiftWrap() { return giftWrap; }
    public void setGiftWrap(Boolean giftWrap) { this.giftWrap = giftWrap; }

    public String getGiftRecipientName() { return giftRecipientName; }
    public void setGiftRecipientName(String giftRecipientName) { this.giftRecipientName = giftRecipientName; }

    public String getGiftOccasion() { return giftOccasion; }
    public void setGiftOccasion(String giftOccasion) { this.giftOccasion = giftOccasion; }

    public String getGiftMessage() { return giftMessage; }
    public void setGiftMessage(String giftMessage) { this.giftMessage = giftMessage; }

    public Double getGiftWrapFee() { return giftWrapFee; }
    public void setGiftWrapFee(Double giftWrapFee) { this.giftWrapFee = giftWrapFee; }


    public void addItem(OrderItem item) {
        items.add(item);
        item.setOrder(this);
    }
}
