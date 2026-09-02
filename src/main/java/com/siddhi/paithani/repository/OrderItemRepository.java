package com.siddhi.paithani.repository;

import com.siddhi.paithani.entity.OrderItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {

    @Query("SELECT o FROM OrderItem o WHERE o.product.id = :productId")
    List<OrderItem> findByProductId(@Param("productId") Long productId);
}
