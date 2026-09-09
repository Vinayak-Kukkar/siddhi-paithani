package com.siddhi.paithani.repository;

import com.siddhi.paithani.entity.CustomerPhotoReview;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CustomerPhotoReviewRepository extends JpaRepository<CustomerPhotoReview, Long> {
    List<CustomerPhotoReview> findByIsApprovedTrueOrderByCreatedAtDesc();
    List<CustomerPhotoReview> findByIsApprovedTrueAndOccasionTagOrderByCreatedAtDesc(String occasionTag);
    List<CustomerPhotoReview> findByProductIdAndIsApprovedTrue(Long productId);
}
