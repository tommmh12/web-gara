package com.webgara.module.inventory.controller;

import com.webgara.common.dto.ApiResponse;
import com.webgara.module.inventory.dto.InventoryTransactionRequest;
import com.webgara.module.inventory.dto.InventoryTransactionResponse;
import com.webgara.module.inventory.model.TransactionType;
import com.webgara.module.inventory.service.InventoryTransactionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/inventory-transactions")
@RequiredArgsConstructor
public class InventoryTransactionController {

    private final InventoryTransactionService transactionService;

    @PostMapping
    @PreAuthorize("hasAnyRole('RECEPTIONIST', 'MANAGER')")
    public ResponseEntity<ApiResponse<InventoryTransactionResponse>> createTransaction(
            @Valid @RequestBody InventoryTransactionRequest request) {
        String performedByUserId = getCurrentUserId();
        InventoryTransactionResponse response = transactionService.recordTransaction(request, performedByUserId);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.created(response));
    }

    @GetMapping("/part/{partId}")
    @PreAuthorize("hasAnyRole('RECEPTIONIST', 'TECHNICIAN', 'MANAGER')")
    public ResponseEntity<ApiResponse<Page<InventoryTransactionResponse>>> getPartHistory(
            @PathVariable String partId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir) {

        Sort sort = sortDir.equalsIgnoreCase(Sort.Direction.ASC.name())
                ? Sort.by(sortBy).ascending()
                : Sort.by(sortBy).descending();
        Pageable pageable = PageRequest.of(page, size, sort);

        Page<InventoryTransactionResponse> response = transactionService.getTransactionHistory(partId, pageable);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('RECEPTIONIST', 'MANAGER')")
    public ResponseEntity<ApiResponse<Page<InventoryTransactionResponse>>> listTransactions(
            @RequestParam String garageId,
            @RequestParam(required = false) TransactionType type,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir) {

        Sort sort = sortDir.equalsIgnoreCase(Sort.Direction.ASC.name())
                ? Sort.by(sortBy).ascending()
                : Sort.by(sortBy).descending();
        Pageable pageable = PageRequest.of(page, size, sort);

        Page<InventoryTransactionResponse> response;
        if (type != null) {
            response = transactionService.listByType(type, garageId, pageable);
        } else {
            response = transactionService.listByGarage(garageId, pageable);
        }

        return ResponseEntity.ok(ApiResponse.success(response));
    }

    private String getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated()) {
            return authentication.getName();
        }
        throw new RuntimeException("User not authenticated");
    }
}
