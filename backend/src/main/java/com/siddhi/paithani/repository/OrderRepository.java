package com.siddhi.paithani.repository;

import com.siddhi.paithani.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {

    Optional<Order> findByOrderNumber(String orderNumber);

    List<Order> findAllByOrderByCreatedAtDesc();

    List<Order> findByEmailIgnoreCaseOrMobileOrderByCreatedAtDesc(String email, String mobile);

    List<Order> findByOrderNumberIgnoreCaseOrMobileOrderByCreatedAtDesc(String orderNumber, String mobile);

    boolean existsByEmailIgnoreCase(String email);
}

