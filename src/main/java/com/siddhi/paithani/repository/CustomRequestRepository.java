package com.siddhi.paithani.repository;

import com.siddhi.paithani.entity.CustomRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CustomRequestRepository extends JpaRepository<CustomRequest, Long> {
    List<CustomRequest> findAllByOrderByCreatedAtDesc();
}
