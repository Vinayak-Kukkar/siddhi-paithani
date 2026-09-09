package com.siddhi.paithani.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

@Entity
@Table(name = "customer")
public class Customer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long customerId;

    @NotBlank(message = "Customer name is required")
    @Size(min = 3, max = 50, message = "Customer name must be between 3 and 50 characters")
    @Column(name = "customer_name")
    private String customerName;

    @NotBlank(message = "Mobile number is required")
    @Pattern(regexp = "^[0-9]{10}$", message = "Mobile number must be exactly 10 digits")
    @Column(name = "mobile")
    private String mobile;

    @NotBlank(message = "Email is required")
    @Email(message = "Please enter a valid email address")
    @Column(name = "email")
    private String email;

    @NotBlank(message = "Address is required")
    @Column(name = "address")
    private String address;

    @NotBlank(message = "City is required")
    @Column(name = "city")
    private String city;

    @NotBlank(message = "PIN code is required")
    @Pattern(regexp = "^[0-9]{6}$", message = "PIN code must be exactly 6 digits")
    @Column(name = "pincode")
    private String pincode;

    @Column(name = "password")
    private String password;

    @Column(name = "loyalty_points")
    private Integer loyaltyPoints = 100; // 100 Gold Points Welcome Gift

    @Column(name = "referral_code", unique = true)
    private String referralCode;

    @Column(name = "referred_by_code")
    private String referredByCode;

    @Column(name = "total_referrals_count")
    private Integer totalReferralsCount = 0;

    public Customer() {
    }

    public Customer(Long customerId, String customerName, String mobile, String email,
                    String address, String city, String pincode) {
        this.customerId = customerId;
        this.customerName = customerName;
        this.mobile = mobile;
        this.email = email;
        this.address = address;
        this.city = city;
        this.pincode = pincode;
    }

    // Alias methods for id property access
    public Long getId() {
        return customerId;
    }

    public void setId(Long id) {
        this.customerId = id;
    }

    public Long getCustomerId() {
        return customerId;
    }

    public void setCustomerId(Long customerId) {
        this.customerId = customerId;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    // Property aliases for firstName & lastName compatibility
    public String getFirstName() {
        if (customerName == null) return "";
        String[] parts = customerName.trim().split("\\s+", 2);
        return parts[0];
    }

    public void setFirstName(String firstName) {
        if (this.customerName == null || this.customerName.isEmpty()) {
            this.customerName = firstName;
        } else {
            String[] parts = this.customerName.trim().split("\\s+", 2);
            String last = parts.length > 1 ? parts[1] : "";
            this.customerName = (firstName + " " + last).trim();
        }
    }

    public String getLastName() {
        if (customerName == null) return "";
        String[] parts = customerName.trim().split("\\s+", 2);
        return parts.length > 1 ? parts[1] : "";
    }

    public void setLastName(String lastName) {
        if (this.customerName == null || this.customerName.isEmpty()) {
            this.customerName = lastName;
        } else {
            String[] parts = this.customerName.trim().split("\\s+", 2);
            String first = parts[0];
            this.customerName = (first + " " + lastName).trim();
        }
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getPincode() {
        return pincode;
    }

    public void setPincode(String pincode) {
        this.pincode = pincode;
    }

    public Integer getLoyaltyPoints() {
        return loyaltyPoints != null ? loyaltyPoints : 0;
    }

    public void setLoyaltyPoints(Integer loyaltyPoints) {
        this.loyaltyPoints = loyaltyPoints;
    }

    public String getReferralCode() {
        return referralCode;
    }

    public void setReferralCode(String referralCode) {
        this.referralCode = referralCode;
    }

    public String getReferredByCode() {
        return referredByCode;
    }

    public void setReferredByCode(String referredByCode) {
        this.referredByCode = referredByCode;
    }

    public Integer getTotalReferralsCount() {
        return totalReferralsCount != null ? totalReferralsCount : 0;
    }

    public void setTotalReferralsCount(Integer totalReferralsCount) {
        this.totalReferralsCount = totalReferralsCount;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @Override
    public String toString() {
        return "Customer [customerId=" + customerId +
                ", customerName=" + customerName +
                ", mobile=" + mobile +
                ", email=" + email +
                ", address=" + address +
                ", city=" + city +
                ", pincode=" + pincode + "]";
    }
}