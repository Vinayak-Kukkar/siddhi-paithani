package com.siddhi.paithani.controller;

import com.siddhi.paithani.entity.Product;
import com.siddhi.paithani.service.ProductService;
import com.siddhi.paithani.service.WishlistService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class WishlistController {

    private final WishlistService wishlistService;
    private final ProductService productService;

    @Autowired
    public WishlistController(WishlistService wishlistService, ProductService productService) {
        this.wishlistService = wishlistService;
        this.productService = productService;
    }

    @GetMapping("/wishlist")
    public String viewWishlist(Model model) {
        model.addAttribute("wishlistItems", wishlistService.getWishlistItems());
        return "wishlist";
    }

    @PostMapping("/wishlist/toggle")
    public String toggleWishlist(@RequestParam("productId") Long productId,
                                 HttpServletRequest request,
                                 RedirectAttributes redirectAttributes) {
        Product product = productService.getProductById(productId);
        if (product != null) {
            boolean added = wishlistService.toggleWishlist(product);
            if (added) {
                redirectAttributes.addFlashAttribute("wishlistSuccess", "💖 '" + product.getName() + "' added to your Wishlist!");
            } else {
                redirectAttributes.addFlashAttribute("wishlistSuccess", "Removed from Wishlist.");
            }
        }
        String referer = request.getHeader("Referer");
        return "redirect:" + (referer != null ? referer : "/wishlist");
    }

    @GetMapping("/wishlist/remove/{id}")
    public String removeFromWishlist(@PathVariable("id") Long id, RedirectAttributes redirectAttributes) {
        wishlistService.removeFromWishlist(id);
        redirectAttributes.addFlashAttribute("wishlistSuccess", "Saree removed from Wishlist.");
        return "redirect:/wishlist";
    }

    @GetMapping("/wishlist/toggle-alert/{id}")
    public String togglePriceAlert(@PathVariable("id") Long id, RedirectAttributes redirectAttributes) {
        wishlistService.togglePriceAlert(id);
        redirectAttributes.addFlashAttribute("wishlistSuccess", "🔔 Wishlist price drop alert preferences updated!");
        return "redirect:/wishlist";
    }
}
