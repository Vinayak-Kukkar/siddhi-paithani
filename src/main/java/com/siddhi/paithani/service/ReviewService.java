package com.siddhi.paithani.service;

import com.siddhi.paithani.entity.Review;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface ReviewService {
    Review addReview(Long productId, String reviewerName, String reviewerEmail, int rating, String comment, MultipartFile photo);
    List<Review> getReviewsByProduct(Long productId);
    double getAverageRating(Long productId);
    long getReviewCount(Long productId);
    List<Review> getAllReviews();
    void deleteReview(Long id);
}

