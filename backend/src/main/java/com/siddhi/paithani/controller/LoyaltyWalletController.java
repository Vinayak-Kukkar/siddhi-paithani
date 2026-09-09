package com.siddhi.paithani.controller;

import com.siddhi.paithani.entity.Coupon;
import com.siddhi.paithani.service.LoyaltyWalletService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class LoyaltyWalletController {

    private final LoyaltyWalletService loyaltyWalletService;

    @Autowired
    public LoyaltyWalletController(LoyaltyWalletService loyaltyWalletService) {
        this.loyaltyWalletService = loyaltyWalletService;
    }

    @GetMapping({"/wallet", "/customer/wallet", "/loyalty-wallet"})
    public String viewLoyaltyWallet(@RequestParam(value = "email", required = false) String email,
                                    HttpSession session,
                                    Model model) {
        String activeEmail = email;
        if (activeEmail == null || activeEmail.trim().isEmpty()) {
            activeEmail = (String) session.getAttribute("customerEmail");
        }
        if (activeEmail == null || activeEmail.trim().isEmpty()) {
            activeEmail = "kukkarvinayak11@gmail.com";
        }

        int pointsBalance = loyaltyWalletService.getCustomerLoyaltyBalance(activeEmail);
        double rupeeValue = pointsBalance * 1.0;

        model.addAttribute("customerEmail", activeEmail);
        model.addAttribute("pointsBalance", pointsBalance);
        model.addAttribute("rupeeValue", rupeeValue);
        return "wallet";
    }

    @PostMapping("/wallet/redeem")
    public String redeemPoints(@RequestParam("email") String email,
                               @RequestParam("points") int points,
                               HttpSession session,
                               RedirectAttributes redirectAttributes) {
        try {
            Coupon voucher = loyaltyWalletService.redeemPointsForVoucher(email, points);
            session.setAttribute("appliedCoupon", voucher.getCode());
            session.setAttribute("discountAmount", voucher.getDiscountValue());
            redirectAttributes.addFlashAttribute("walletSuccess", "👑 Successfully redeemed " + points + " Gold Points for ₹" + points + " OFF! Voucher Code: " + voucher.getCode() + " applied to your cart!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("walletError", "❌ " + e.getMessage());
        }
        return "redirect:/wallet?email=" + email;
    }
}
