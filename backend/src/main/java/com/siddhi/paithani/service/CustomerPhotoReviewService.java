package com.siddhi.paithani.service;

import com.siddhi.paithani.entity.CustomerPhotoReview;
import com.siddhi.paithani.repository.CustomerPhotoReviewRepository;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CustomerPhotoReviewService {

    private final CustomerPhotoReviewRepository repository;

    @Autowired
    public CustomerPhotoReviewService(CustomerPhotoReviewRepository repository) {
        this.repository = repository;
    }

    @PostConstruct
    public void seedInitialGalleryReviews() {
        if (repository.count() == 0) {
            repository.save(new CustomerPhotoReview(
                "Pooja Deshmukh",
                "Pune, MH",
                "https://images.unsplash.com/photo-1610030469983-98e550d6193c?auto=format&fit=crop&w=800&q=80",
                "Royal Maharani Paithani (Magenta Silk)",
                13L,
                "Royal Bridal Perfection for my Wedding Day!",
                "Wearing Siddhi Paithani's royal magenta saree on my wedding day was a dream come true! The real gold zari peacock pallu and rich silk weave received endless compliments. Truly the pride of Yeola!",
                5,
                "#SiddhiPaithaniBride"
            ));

            repository.save(new CustomerPhotoReview(
                "Aarti Kulkarni",
                "Nashik, MH",
                "https://images.unsplash.com/photo-1617627143750-d86bc21e42bb?auto=format&fit=crop&w=800&q=80",
                "Yeola Pure Handloom Silk (Emerald Green)",
                14L,
                "Exquisite Craftsmanship & Vibrant Colors!",
                "Bought this gorgeous emerald green saree for Diwali puja. The authentic handloom texture and divine sheen are unmatched. Received order in 2 days with beautiful gift packaging!",
                5,
                "#FestiveElegance"
            ));

            repository.save(new CustomerPhotoReview(
                "Sneha Patil",
                "Mumbai, MH",
                "https://images.unsplash.com/photo-1583391733956-3750e0ff4e8b?auto=format&fit=crop&w=800&q=80",
                "Classic Peacock Pallu Paithani (Royal Blue)",
                15L,
                "Stunning Traditional Craftsmanship!",
                "Wore this royal blue Paithani for my sister's engagement ceremony. Everyone loved the intricate peacock border. Thank you Siddhi Paithani team for fast dispatch and silk certificate!",
                5,
                "#MaharashtrianGrace"
            ));

            repository.save(new CustomerPhotoReview(
                "Mrunal Joshi",
                "Nagpur, MH",
                "https://images.unsplash.com/photo-1596783074918-c84cb06531ca?auto=format&fit=crop&w=800&q=80",
                "Brocade Tissue Silk Paithani (Golden Yellow)",
                16L,
                "Divine Golden Elegance for Dohale Jevan!",
                "The golden tissue brocade saree felt like pure royalty. Soft fabric, breathable pure silk, and genuine quality certificate enclosed. Highly recommend to every bride-to-be!",
                5,
                "#SiddhiPaithaniBride"
            ));
        }
    }

    public List<CustomerPhotoReview> getApprovedReviews() {
        return repository.findByIsApprovedTrueOrderByCreatedAtDesc();
    }

    public List<CustomerPhotoReview> getApprovedReviewsByTag(String tag) {
        if (tag == null || tag.trim().isEmpty() || tag.equalsIgnoreCase("all")) {
            return getApprovedReviews();
        }
        return repository.findByIsApprovedTrueAndOccasionTagOrderByCreatedAtDesc(tag.trim());
    }

    public CustomerPhotoReview saveReview(CustomerPhotoReview review) {
        return repository.save(review);
    }

    public void approveReview(Long id) {
        repository.findById(id).ifPresent(r -> {
            r.setApproved(true);
            repository.save(r);
        });
    }

    public void deleteReview(Long id) {
        repository.deleteById(id);
    }
}
