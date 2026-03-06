package com.webgara.module.garage.repository;

import com.webgara.module.garage.model.Garage;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface GarageRepository extends MongoRepository<Garage, String> {
    Optional<Garage> findBySlug(String slug);
    Page<Garage> findByIsActiveTrue(Pageable pageable);
    boolean existsBySlug(String slug);
}
