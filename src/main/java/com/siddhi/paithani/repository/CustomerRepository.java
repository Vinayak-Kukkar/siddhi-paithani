package com.siddhi.paithani.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.siddhi.paithani.entity.Customer;

@Repository
public interface CustomerRepository extends JpaRepository<Customer, Long> {

    List<Customer> findByCustomerNameContainingIgnoreCase(String keyword);

    Optional<Customer> findByEmail(String email);

    Optional<Customer> findByEmailIgnoreCase(String email);

    Optional<Customer> findByMobile(String mobile);

    Optional<Customer> findByReferralCode(String referralCode);

    List<Customer> findByReferredByCode(String referredByCode);
}