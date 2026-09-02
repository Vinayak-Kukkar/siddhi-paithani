package com.siddhi.paithani.service;

import com.siddhi.paithani.entity.Coupon;
import com.siddhi.paithani.entity.Customer;
import com.siddhi.paithani.repository.CouponRepository;
import com.siddhi.paithani.repository.CustomerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class LoyaltyWalletService {

    private final CustomerRepository customerRepository;
    private final CouponRepository couponRepository;

    @Autowired
    public LoyaltyWalletService(CustomerRepository customerRepository, CouponRepository couponRepository) {
        this.customerRepository = customerRepository;
        this.couponRepository = couponRepository;
    }

    /**
     * Award 1 Loyalty Gold Point for every ₹100 spent on an order
     */
    public Customer awardPointsForOrder(String email, double orderTotalAmount) {
        if (email == null || email.trim().isEmpty()) return null;
        Optional<Customer> opt = customerRepository.findByEmailIgnoreCase(email.trim());
        if (opt.isPresent()) {
            Customer customer = opt.get();
            int earnedPoints = (int) Math.floor(orderTotalAmount / 100.0);
            int currentPoints = customer.getLoyaltyPoints();
            customer.setLoyaltyPoints(currentPoints + earnedPoints);
            return customerRepository.save(customer);
        }
        return null;
    }

    public Customer addPoints(Customer customer, int points, String reason) {
        if (customer == null) return null;
        int current = customer.getLoyaltyPoints();
        customer.setLoyaltyPoints(current + points);
        return customerRepository.save(customer);
    }

    /**
     * Get or create customer loyalty balance (default 100 Gold Points welcome bonus)
     */
    public int getCustomerLoyaltyBalance(String email) {
        if (email == null || email.trim().isEmpty()) return 100;
        Optional<Customer> opt = customerRepository.findByEmailIgnoreCase(email.trim());
        if (opt.isPresent()) {
            return opt.get().getLoyaltyPoints();
        }
        return 100; // Default welcome points
    }

    /**
     * Redeem Loyalty Gold Points into a Discount Coupon Voucher (1 Point = ₹1 OFF)
     */
    public Coupon redeemPointsForVoucher(String email, int pointsToRedeem) {
        if (pointsToRedeem < 50) {
            throw new IllegalArgumentException("Minimum 50 Gold Points required to generate a discount voucher!");
        }

        Customer customer = null;
        if (email != null && !email.trim().isEmpty()) {
            Optional<Customer> opt = customerRepository.findByEmailIgnoreCase(email.trim());
            if (opt.isPresent()) {
                customer = opt.get();
                if (customer.getLoyaltyPoints() < pointsToRedeem) {
                    throw new IllegalArgumentException("Insufficient Gold Points balance! Available: " + customer.getLoyaltyPoints());
                }
                customer.setLoyaltyPoints(customer.getLoyaltyPoints() - pointsToRedeem);
                customerRepository.save(customer);
            }
        }

        // Generate unique discount voucher coupon
        String couponCode = "GOLD-" + pointsToRedeem + "-" + UUID.randomUUID().toString().substring(0, 4).toUpperCase();
        Coupon voucher = new Coupon();
        voucher.setCode(couponCode);
        voucher.setDiscountType("FLAT_AMOUNT");
        voucher.setDiscountValue((double) pointsToRedeem);
        voucher.setMinOrderAmount(500.0);
        voucher.setActive(true);
        voucher.setDescription("Loyalty Gold Voucher: ₹" + pointsToRedeem + " OFF");

        return couponRepository.save(voucher);
    }
}
