package com.siddhi.paithani.service;

import com.siddhi.paithani.entity.Customer;
import com.siddhi.paithani.repository.CustomerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Optional;

@Service
public class ReferralService {

    private final CustomerRepository customerRepository;
    private final LoyaltyWalletService loyaltyWalletService;

    @Autowired
    public ReferralService(CustomerRepository customerRepository, LoyaltyWalletService loyaltyWalletService) {
        this.customerRepository = customerRepository;
        this.loyaltyWalletService = loyaltyWalletService;
    }

    public String getOrCreateReferralCode(Customer customer) {
        if (customer == null) return "SP-GUEST-500";
        if (customer.getReferralCode() != null && !customer.getReferralCode().trim().isEmpty()) {
            return customer.getReferralCode();
        }

        String nameClean = customer.getCustomerName() != null 
                ? customer.getCustomerName().trim().replaceAll("[^a-zA-Z]", "").toUpperCase() 
                : "PAITHANI";
        if (nameClean.length() > 8) {
            nameClean = nameClean.substring(0, 8);
        }

        String code = "SP-" + nameClean + "-" + (customer.getCustomerId() != null ? customer.getCustomerId() : (int)(Math.random() * 900 + 100));
        customer.setReferralCode(code);
        customerRepository.save(customer);
        return code;
    }

    public String generateWhatsAppShareLink(Customer customer) {
        String code = getOrCreateReferralCode(customer);
        String text = "👑 *SIDDHI PAITHANI - EXCLUSIVE ₹500 GIFT VOUCHER FOR YOU!*\n"
                + "-----------------------------------------\n"
                + "Hello! Join Siddhi Paithani to shop 100% Authentic Handwoven Yeola Silk Sarees with Official Silk Mark India Certification.\n\n"
                + "🎁 *Use My Exclusive Referral Code:* *" + code + "*\n"
                + "✨ *Get ₹500 Instant Discount on Your First Order!*\n\n"
                + "👉 *Claim Your ₹500 Discount Here:* http://localhost:8084/register?ref=" + code + "\n"
                + "-----------------------------------------\n"
                + "Handwoven in Yeola, Maharashtra • 100% Pure Silk & Real Gold Zari";

        return "https://api.whatsapp.com/send?text=" + URLEncoder.encode(text, StandardCharsets.UTF_8);
    }

    public void processReferralOnSignup(Customer referee, String referralCode) {
        if (referralCode == null || referralCode.trim().isEmpty() || referee == null) return;
        String cleanCode = referralCode.trim();
        
        Optional<Customer> referrerOpt = customerRepository.findByReferralCode(cleanCode);
        if (referrerOpt.isPresent()) {
            Customer referrer = referrerOpt.get();
            referee.setReferredByCode(cleanCode);
            
            // Increment referrer stats & award ₹500 (500 loyalty points)
            referrer.setTotalReferralsCount(referrer.getTotalReferralsCount() + 1);
            loyaltyWalletService.addPoints(referrer, 500, "Referral Bonus for inviting " + referee.getCustomerName() + " (" + cleanCode + ")");
            customerRepository.save(referrer);

            // Welcome bonus points for new referee customer
            loyaltyWalletService.addPoints(referee, 500, "Welcome Gift Coupon for joining via Referral Code " + cleanCode);
        }
    }

    public List<Customer> getReferredFriends(Customer referrer) {
        if (referrer == null || referrer.getReferralCode() == null) return List.of();
        return customerRepository.findByReferredByCode(referrer.getReferralCode());
    }
}
