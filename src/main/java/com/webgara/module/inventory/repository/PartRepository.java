package com.webgara.module.inventory.repository;

import com.webgara.module.inventory.model.Part;
import com.webgara.module.inventory.model.PartCategory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PartRepository extends MongoRepository<Part, String> {
    Optional<Part> findByCode(String code);

    Page<Part> findByGarageId(String garageId, Pageable pageable);

    Page<Part> findByGarageIdAndCategory(String garageId, PartCategory category, Pageable pageable);

    List<Part> findByGarageIdAndQuantityLessThanEqualMinQuantity(String garageId);

    Page<Part> findByNameContainingIgnoreCase(String name, Pageable pageable);

    Page<Part> findByGarageIdAndIsActive(String garageId, Boolean isActive, Pageable pageable);
}
