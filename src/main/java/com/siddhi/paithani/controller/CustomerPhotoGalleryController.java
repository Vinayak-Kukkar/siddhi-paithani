package com.siddhi.paithani.controller;

import com.siddhi.paithani.entity.CustomerPhotoReview;
import com.siddhi.paithani.service.CustomerPhotoReviewService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
public class CustomerPhotoGalleryController {

    private final CustomerPhotoReviewService reviewService;

    @Autowired
    public CustomerPhotoGalleryController(CustomerPhotoReviewService reviewService) {
        this.reviewService = reviewService;
    }

    @GetMapping({"/gallery", "/siddhi-brides", "/customer-photos"})
    public String showGalleryPage(@RequestParam(value = "tag", required = false, defaultValue = "all") String tag,
                                  Model model) {
        List<CustomerPhotoReview> reviews = reviewService.getApprovedReviewsByTag(tag);
        model.addAttribute("reviews", reviews);
        model.addAttribute("activeTag", tag);
        return "gallery";
    }

    @PostMapping("/api/gallery/upload")
    public String uploadCustomerPhotoReview(@RequestParam("customerName") String customerName,
                                            @RequestParam("customerCity") String customerCity,
                                            @RequestParam("photoUrl") String photoUrl,
                                            @RequestParam("sareeName") String sareeName,
                                            @RequestParam(value = "productId", required = false, defaultValue = "13") Long productId,
                                            @RequestParam("reviewTitle") String reviewTitle,
                                            @RequestParam("reviewText") String reviewText,
                                            @RequestParam(value = "rating", defaultValue = "5") int rating,
                                            @RequestParam("occasionTag") String occasionTag,
                                            RedirectAttributes redirectAttributes) {
        
        CustomerPhotoReview review = new CustomerPhotoReview(
            customerName.trim(),
            customerCity.trim(),
            photoUrl.trim().isEmpty() ? "https://images.unsplash.com/photo-1610030469983-98e550d6193c?auto=format&fit=crop&w=800&q=80" : photoUrl.trim(),
            sareeName.trim(),
            productId,
            reviewTitle.trim(),
            reviewText.trim(),
            rating,
            occasionTag.trim()
        );
        
        reviewService.saveReview(review);
        redirectAttributes.addFlashAttribute("successMessage", "✨ Thank you! Your #SiddhiPaithaniBride photo review has been published successfully!");
        return "redirect:/gallery";
    }

    @GetMapping("/admin/gallery")
    public String adminGalleryPanel(HttpSession session, Model model) {
        if (!Boolean.TRUE.equals(session.getAttribute("isAdminLoggedIn"))) {
            return "redirect:/admin/login";
        }
        model.addAttribute("reviews", reviewService.getApprovedReviews());
        return "admin-gallery";
    }

    @PostMapping("/admin/gallery/delete/{id}")
    public String deletePhotoReview(@PathVariable("id") Long id, HttpSession session, RedirectAttributes redirectAttributes) {
        if (!Boolean.TRUE.equals(session.getAttribute("isAdminLoggedIn"))) {
            return "redirect:/admin/login";
        }
        reviewService.deleteReview(id);
        redirectAttributes.addFlashAttribute("adminSuccess", "Photo review deleted successfully.");
        return "redirect:/admin/gallery";
    }
}
