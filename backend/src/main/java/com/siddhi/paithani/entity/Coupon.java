package com.siddhi.paithani.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "coupons")
@Data
public class Coupon {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Coupon code is required")
    @Column(name = "code", unique = true, nullable = false)
    private String code;

    @NotBlank(message = "Discount type is required")
    @Column(name = "discount_type", nullable = false)
    private String discountType = "PERCENTAGE"; // PERCENTAGE or FLAT_AMOUNT

    @NotNull(message = "Discount value is required")
    @Column(name = "discount_value", nullable = false)
    private Double discountValue; // e.g. 10.0 for 10% or 500.0 for ₹500

    @Column(name = "min_order_amount")
    private Double minOrderAmount = 0.0; // Minimum order subtotal required

    @Column(name = "used_count")
    private Integer usedCount = 0;

    @Column(name = "max_uses")
    private Integer maxUses = 1000;

    @Column(name = "is_active")
    private Boolean active = true;

    @Column(name = "description")
    private String description;

    @Column(name = "created_at")
    private LocalDateTime createdAt = LocalDateTime.now();

    public Coupon() {}

    public Coupon(Long id, String code, String discountType, Double discountValue, Double minOrderAmount, Integer usedCount, Integer maxUses, Boolean active, String description, LocalDateTime createdAt) {
        this.id = id;
        this.code = code;
        this.discountType = discountType;
        this.discountValue = discountValue;
        this.minOrderAmount = minOrderAmount;
        this.usedCount = usedCount;
        this.maxUses = maxUses;
        this.active = active;
        this.description = description;
        this.createdAt = createdAt;
    }
    public void setId(Long id) { this.id = id; }

    public String getCode() { return code; }
    public void setCode(String code) { this.code = code; }

    public String getDiscountType() { return discountType; }
    public void setDiscountType(String discountType) { this.discountType = discountType; }

    public Double getDiscountValue() { return discountValue; }
    public void setDiscountValue(Double discountValue) { this.discountValue = discountValue; }

    public Double getMinOrderAmount() { return minOrderAmount; }
    public void setMinOrderAmount(Double minOrderAmount) { this.minOrderAmount = minOrderAmount; }

    public Integer getUsedCount() { return usedCount; }
    public void setUsedCount(Integer usedCount) { this.usedCount = usedCount; }

    public Integer getMaxUses() { return maxUses; }
    public void setMaxUses(Integer maxUses) { this.maxUses = maxUses; }

    public Boolean getActive() { return active; }
    public void setActive(Boolean active) { this.active = active; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
