package com.webgara.module.service.repository;

import com.webgara.module.service.model.ServiceItem;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ServiceItemRepository extends MongoRepository<ServiceItem, String> {
    Page<ServiceItem> findByGarageIdAndIsActiveTrue(String garageId, Pageable pageable);
    Page<ServiceItem> findByGarageId(String garageId, Pageable pageable);
    Page<ServiceItem> findByCategoryAndIsActiveTrue(String category, Pageable pageable);
}
