package com.raphau.trafficgenerator.dao;

import com.raphau.trafficgenerator.entity.Endpoint;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface EndpointRepository extends JpaRepository<Endpoint, Integer> {
    Optional<Endpoint> findByEndpoint(String endpoint);
}
