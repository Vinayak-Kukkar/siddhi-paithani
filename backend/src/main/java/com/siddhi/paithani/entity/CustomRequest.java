package com.siddhi.paithani.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "custom_requests")
public class CustomRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String customerName;
    private String mobile;
    private String sareeName;
    private String blouseSize; // Unstitched, S, M, L, XL, XXL, Custom
    private String customMeasurements;
    private String preferredZariMotif; // Peacock, Lotus, Parrot, Asavali
    private String preferredColor;
    
    @Column(length = 1000)
    private String specialInstructions;
    
    private LocalDateTime createdAt;
    private String status; // PENDING, ARTISAN_REVIEWING, IN_WEAVING, COMPLETED

    public CustomRequest() {
        this.createdAt = LocalDateTime.now();
        this.status = "PENDING";
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getCustomerName() { return customerName; }
    public void setCustomerName(String customerName) { this.customerName = customerName; }

    public String getMobile() { return mobile; }
    public void setMobile(String mobile) { this.mobile = mobile; }

    public String getSareeName() { return sareeName; }
    public void setSareeName(String sareeName) { this.sareeName = sareeName; }

    public String getBlouseSize() { return blouseSize; }
    public void setBlouseSize(String blouseSize) { this.blouseSize = blouseSize; }

    public String getCustomMeasurements() { return customMeasurements; }
    public void setCustomMeasurements(String customMeasurements) { this.customMeasurements = customMeasurements; }

    public String getPreferredZariMotif() { return preferredZariMotif; }
    public void setPreferredZariMotif(String preferredZariMotif) { this.preferredZariMotif = preferredZariMotif; }

    public String getPreferredColor() { return preferredColor; }
    public void setPreferredColor(String preferredColor) { this.preferredColor = preferredColor; }

    public String getSpecialInstructions() { return specialInstructions; }
    public void setSpecialInstructions(String specialInstructions) { this.specialInstructions = specialInstructions; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}
