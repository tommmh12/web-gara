package com.webgara.module.inventory.service;

import com.webgara.module.inventory.dto.InventoryTransactionRequest;
import com.webgara.module.inventory.dto.InventoryTransactionResponse;
import com.webgara.module.inventory.model.TransactionType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface InventoryTransactionService {
    InventoryTransactionResponse recordTransaction(InventoryTransactionRequest request, String performedByUserId);

    Page<InventoryTransactionResponse> getTransactionHistory(String partId, Pageable pageable);

    Page<InventoryTransactionResponse> listByGarage(String garageId, Pageable pageable);

    Page<InventoryTransactionResponse> listByType(TransactionType type, String garageId, Pageable pageable);
}
