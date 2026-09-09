package com.siddhi.paithani.service.impl;

import com.siddhi.paithani.entity.Product;
import com.siddhi.paithani.entity.Review;
import com.siddhi.paithani.repository.OrderRepository;
import com.siddhi.paithani.repository.ProductRepository;
import com.siddhi.paithani.repository.ReviewRepository;
import com.siddhi.paithani.service.ReviewService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.Base64;
import java.util.List;

@Service
public class ReviewServiceImpl implements ReviewService {

    private final ReviewRepository reviewRepository;
    private final ProductRepository productRepository;
    private final OrderRepository orderRepository;

    @Autowired
    public ReviewServiceImpl(ReviewRepository reviewRepository, ProductRepository productRepository, OrderRepository orderRepository) {
        this.reviewRepository = reviewRepository;
        this.productRepository = productRepository;
        this.orderRepository = orderRepository;
    }

    @Override
    public Review addReview(Long productId, String reviewerName, String reviewerEmail, int rating, String comment, MultipartFile photo) {
        Product product = productRepository.findById(productId).orElse(null);
        if (product == null) {
            throw new IllegalArgumentException("Product not found with ID: " + productId);
        }
        if (rating < 1) rating = 1;
        if (rating > 5) rating = 5;

        Review review = new Review(product, reviewerName, reviewerEmail, rating, comment);

        if (reviewerEmail != null && !reviewerEmail.trim().isEmpty()) {
            boolean isPurchaser = orderRepository.existsByEmailIgnoreCase(reviewerEmail.trim());
            review.setVerifiedBuyer(isPurchaser);
        }

        if (photo != null && !photo.isEmpty()) {
            try {
                String base64Image = "data:" + (photo.getContentType() != null ? photo.getContentType() : "image/jpeg") + ";base64," + Base64.getEncoder().encodeToString(photo.getBytes());
                review.setImageUrl(base64Image);
            } catch (Exception e) {
                // Ignore upload exception
            }
        }

        return reviewRepository.save(review);
    }

    @Override
    public List<Review> getReviewsByProduct(Long productId) {
        return reviewRepository.findByProductIdOrderByCreatedAtDesc(productId);
    }

    @Override
    public double getAverageRating(Long productId) {
        Double avg = reviewRepository.getAverageRatingForProduct(productId);
        return (avg != null) ? Math.round(avg * 10.0) / 10.0 : 4.9;
    }

    @Override
    public long getReviewCount(Long productId) {
        return reviewRepository.countByProductId(productId);
    }

    @Override
    public List<Review> getAllReviews() {
        return reviewRepository.findAll();
    }

    @Override
    public void deleteReview(Long id) {
        reviewRepository.deleteById(id);
    }
}

