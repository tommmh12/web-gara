package com.webgara.module.inventory.repository;

import com.webgara.module.inventory.model.InventoryTransaction;
import com.webgara.module.inventory.model.ReferenceType;
import com.webgara.module.inventory.model.TransactionType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface InventoryTransactionRepository extends MongoRepository<InventoryTransaction, String> {
    Page<InventoryTransaction> findByPartIdOrderByCreatedAtDesc(String partId, Pageable pageable);

    Page<InventoryTransaction> findByGarageIdOrderByCreatedAtDesc(String garageId, Pageable pageable);

    List<InventoryTransaction> findByReferenceTypeAndReferenceId(ReferenceType referenceType, String referenceId);

    Page<InventoryTransaction> findByTypeAndGarageId(TransactionType type, String garageId, Pageable pageable);
}
