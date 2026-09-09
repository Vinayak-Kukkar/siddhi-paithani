package com.siddhi.paithani.controller;

import com.siddhi.paithani.entity.Coupon;
import com.siddhi.paithani.repository.CouponRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/admin/coupons")
public class AdminCouponController {

    @Autowired
    private CouponRepository couponRepository;

    @GetMapping
    public String listCoupons(Model model) {
        List<Coupon> coupons = couponRepository.findAllByOrderByCreatedAtDesc();
        long activeCount = coupons.stream().filter(c -> Boolean.TRUE.equals(c.getActive())).count();
        int totalUses = coupons.stream().mapToInt(c -> c.getUsedCount() != null ? c.getUsedCount() : 0).sum();

        model.addAttribute("coupons", coupons);
        model.addAttribute("activeCount", activeCount);
        model.addAttribute("totalUses", totalUses);
        model.addAttribute("newCoupon", new Coupon());
        return "admin-coupons";
    }

    @PostMapping("/add")
    public String addCoupon(@ModelAttribute Coupon coupon, RedirectAttributes redirectAttributes) {
        if (coupon.getCode() == null || coupon.getCode().trim().isEmpty()) {
            redirectAttributes.addFlashAttribute("errorMessage", "❌ Coupon code cannot be empty!");
            return "redirect:/admin/coupons";
        }

        String cleanCode = coupon.getCode().trim().toUpperCase();
        Optional<Coupon> existing = couponRepository.findByCodeIgnoreCase(cleanCode);
        if (existing.isPresent()) {
            redirectAttributes.addFlashAttribute("errorMessage", "⚠️ Coupon code '" + cleanCode + "' already exists!");
            return "redirect:/admin/coupons";
        }

        coupon.setCode(cleanCode);
        if (coupon.getDiscountType() == null) coupon.setDiscountType("PERCENTAGE");
        if (coupon.getDiscountValue() == null) coupon.setDiscountValue(10.0);
        if (coupon.getMinOrderAmount() == null) coupon.setMinOrderAmount(0.0);
        if (coupon.getMaxUses() == null) coupon.setMaxUses(500);
        if (coupon.getUsedCount() == null) coupon.setUsedCount(0);
        if (coupon.getActive() == null) coupon.setActive(true);

        couponRepository.save(coupon);
        redirectAttributes.addFlashAttribute("successMessage", "✅ Discount Coupon '" + cleanCode + "' created successfully!");
        return "redirect:/admin/coupons";
    }

    @PostMapping("/toggle/{id}")
    public String toggleCouponStatus(@PathVariable("id") Long id, RedirectAttributes redirectAttributes) {
        Coupon coupon = couponRepository.findById(id).orElse(null);
        if (coupon != null) {
            boolean newStatus = !Boolean.TRUE.equals(coupon.getActive());
            coupon.setActive(newStatus);
            couponRepository.save(coupon);
            redirectAttributes.addFlashAttribute("successMessage", "🔄 Coupon '" + coupon.getCode() + "' is now " + (newStatus ? "ACTIVE 🟢" : "DISABLED 🔴"));
        }
        return "redirect:/admin/coupons";
    }

    @PostMapping("/delete/{id}")
    public String deleteCoupon(@PathVariable("id") Long id, RedirectAttributes redirectAttributes) {
        Coupon coupon = couponRepository.findById(id).orElse(null);
        if (coupon != null) {
            String code = coupon.getCode();
            couponRepository.delete(coupon);
            redirectAttributes.addFlashAttribute("successMessage", "🗑️ Coupon '" + code + "' deleted successfully!");
        }
        return "redirect:/admin/coupons";
    }
}
