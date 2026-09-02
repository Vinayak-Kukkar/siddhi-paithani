package com.siddhi.paithani.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "customer_photo_reviews")
public class CustomerPhotoReview {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String customerName;
    private String customerCity;
    
    @Column(length = 1000)
    private String photoUrl;
    
    private String sareeName;
    private Long productId;
    private String reviewTitle;

    @Column(length = 2000)
    private String reviewText;

    private int rating; // 1 to 5 stars
    private String occasionTag; // e.g. #SiddhiPaithaniBride, #FestiveElegance, #MaharashtrianGrace
    private boolean isApproved = true;
    private LocalDateTime createdAt = LocalDateTime.now();

    public CustomerPhotoReview() {}

    public CustomerPhotoReview(String customerName, String customerCity, String photoUrl, String sareeName, 
                               Long productId, String reviewTitle, String reviewText, int rating, String occasionTag) {
        this.customerName = customerName;
        this.customerCity = customerCity;
        this.photoUrl = photoUrl;
        this.sareeName = sareeName;
        this.productId = productId;
        this.reviewTitle = reviewTitle;
        this.reviewText = reviewText;
        this.rating = rating;
        this.occasionTag = occasionTag;
        this.isApproved = true;
        this.createdAt = LocalDateTime.now();
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getCustomerName() { return customerName; }
    public void setCustomerName(String customerName) { this.customerName = customerName; }

    public String getCustomerCity() { return customerCity; }
    public void setCustomerCity(String customerCity) { this.customerCity = customerCity; }

    public String getPhotoUrl() { return photoUrl; }
    public void setPhotoUrl(String photoUrl) { this.photoUrl = photoUrl; }

    public String getSareeName() { return sareeName; }
    public void setSareeName(String sareeName) { this.sareeName = sareeName; }

    public Long getProductId() { return productId; }
    public void setProductId(Long productId) { this.productId = productId; }

    public String getReviewTitle() { return reviewTitle; }
    public void setReviewTitle(String reviewTitle) { this.reviewTitle = reviewTitle; }

    public String getReviewText() { return reviewText; }
    public void setReviewText(String reviewText) { this.reviewText = reviewText; }

    public int getRating() { return rating; }
    public void setRating(int rating) { this.rating = rating; }

    public String getOccasionTag() { return occasionTag; }
    public void setOccasionTag(String occasionTag) { this.occasionTag = occasionTag; }

    public boolean isApproved() { return isApproved; }
    public void setApproved(boolean approved) { isApproved = approved; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
