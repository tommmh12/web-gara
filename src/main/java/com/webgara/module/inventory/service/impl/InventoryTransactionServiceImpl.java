package com.webgara.module.inventory.service.impl;

import com.webgara.common.exception.BadRequestException;
import com.webgara.common.exception.ResourceNotFoundException;
import com.webgara.module.inventory.dto.InventoryTransactionRequest;
import com.webgara.module.inventory.dto.InventoryTransactionResponse;
import com.webgara.module.inventory.mapper.InventoryTransactionMapper;
import com.webgara.module.inventory.model.InventoryTransaction;
import com.webgara.module.inventory.model.Part;
import com.webgara.module.inventory.model.TransactionType;
import com.webgara.module.inventory.repository.InventoryTransactionRepository;
import com.webgara.module.inventory.repository.PartRepository;
import com.webgara.module.inventory.service.InventoryTransactionService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class InventoryTransactionServiceImpl implements InventoryTransactionService {

    private final InventoryTransactionRepository transactionRepository;
    private final PartRepository partRepository;
    private final InventoryTransactionMapper transactionMapper;

    @Override
    @Transactional
    public InventoryTransactionResponse recordTransaction(InventoryTransactionRequest request, String performedByUserId) {
        Part part = partRepository.findById(request.getPartId())
                .orElseThrow(() -> new ResourceNotFoundException("Part", "id", request.getPartId()));

        int previousQuantity = part.getQuantity();
        int newQuantity = previousQuantity + request.getQuantity();

        // Validate quantity doesn't go below 0 for EXPORT transactions
        if (request.getType() == TransactionType.EXPORT && newQuantity < 0) {
            throw new BadRequestException("Cannot export " + request.getQuantity() + " units. Available: " + previousQuantity);
        }

        if (request.getType() == TransactionType.ADJUSTMENT && newQuantity < 0) {
            throw new BadRequestException("Adjustment would result in negative quantity. Current: " + previousQuantity + ", Adjustment: " + request.getQuantity());
        }

        // Update part quantity
        part.setQuantity(newQuantity);
        partRepository.save(part);

        // Create transaction record
        InventoryTransaction transaction = InventoryTransaction.builder()
                .partId(request.getPartId())
                .garageId(request.getGarageId())
                .type(request.getType())
                .quantity(request.getQuantity())
                .previousQuantity(previousQuantity)
                .newQuantity(newQuantity)
                .reason(request.getReason())
                .unitPrice(request.getUnitPrice())
                .referenceType(request.getReferenceType())
                .referenceId(request.getReferenceId())
                .performedBy(performedByUserId)
                .build();

        InventoryTransaction saved = transactionRepository.save(transaction);
        return transactionMapper.toResponse(saved);
    }

    @Override
    public Page<InventoryTransactionResponse> getTransactionHistory(String partId, Pageable pageable) {
        Page<InventoryTransaction> page = transactionRepository.findByPartIdOrderByCreatedAtDesc(partId, pageable);
        return page.map(transactionMapper::toResponse);
    }

    @Override
    public Page<InventoryTransactionResponse> listByGarage(String garageId, Pageable pageable) {
        Page<InventoryTransaction> page = transactionRepository.findByGarageIdOrderByCreatedAtDesc(garageId, pageable);
        return page.map(transactionMapper::toResponse);
    }

    @Override
    public Page<InventoryTransactionResponse> listByType(TransactionType type, String garageId, Pageable pageable) {
        Page<InventoryTransaction> page = transactionRepository.findByTypeAndGarageId(type, garageId, pageable);
        return page.map(transactionMapper::toResponse);
    }
}
