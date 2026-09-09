package com.siddhi.paithani.controller;

import com.siddhi.paithani.entity.Customer;
import com.siddhi.paithani.service.CustomerService;
import com.siddhi.paithani.service.ReferralService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Controller
public class ReferralController {

    private final ReferralService referralService;
    private final CustomerService customerService;

    @Autowired
    public ReferralController(ReferralService referralService, CustomerService customerService) {
        this.referralService = referralService;
        this.customerService = customerService;
    }

    @GetMapping("/referral")
    public String referralDashboard(HttpSession session, Model model) {
        Long customerId = (Long) session.getAttribute("loggedInCustomerId");
        Customer customer = null;

        if (customerId != null) {
            customer = customerService.getCustomerById(customerId);
        }

        if (customer == null) {
            // Demo fallback for testing referral dashboard
            List<Customer> all = customerService.getAllCustomers();
            if (!all.isEmpty()) {
                customer = all.get(0);
            }
        }

        if (customer != null) {
            String referralCode = referralService.getOrCreateReferralCode(customer);
            String whatsAppShareLink = referralService.generateWhatsAppShareLink(customer);
            List<Customer> referredFriends = referralService.getReferredFriends(customer);
            int totalEarnings = customer.getTotalReferralsCount() * 500;

            model.addAttribute("customer", customer);
            model.addAttribute("referralCode", referralCode);
            model.addAttribute("referralUrl", "http://localhost:8084/register?ref=" + referralCode);
            model.addAttribute("whatsAppShareLink", whatsAppShareLink);
            model.addAttribute("referredFriends", referredFriends);
            model.addAttribute("totalEarnings", totalEarnings);
        } else {
            model.addAttribute("referralCode", "SP-PAITHANI-500");
            model.addAttribute("referralUrl", "http://localhost:8084/register?ref=SP-PAITHANI-500");
            model.addAttribute("whatsAppShareLink", "https://api.whatsapp.com/send?text=Join%20Siddhi%20Paithani");
            model.addAttribute("totalEarnings", 0);
        }

        return "referral-dashboard";
    }
}
