package com.webgara.module.repair.controller;

import com.webgara.common.dto.ApiResponse;
import com.webgara.module.repair.dto.InspectionChecklistDTO;
import com.webgara.module.repair.dto.ProposedItemDTO;
import com.webgara.module.repair.dto.RepairOrderRequest;
import com.webgara.module.repair.dto.RepairOrderResponse;
import com.webgara.module.repair.model.RepairOrderStatus;
import com.webgara.module.repair.service.RepairOrderService;
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

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/api/v1/repair-orders")
@RequiredArgsConstructor
public class RepairOrderController {

    private final RepairOrderService repairOrderService;

    @PostMapping
    @PreAuthorize("hasAnyRole('RECEPTIONIST', 'TECHNICIAN', 'MANAGER')")
    public ResponseEntity<ApiResponse<RepairOrderResponse>> createRepairOrder(
            @Valid @RequestBody RepairOrderRequest request) {
        RepairOrderResponse response = repairOrderService.createFromAppointment(
                request.getAppointmentId(), request);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.created(response));
    }

    @GetMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<RepairOrderResponse>> getRepairOrder(
            @PathVariable String id) {
        RepairOrderResponse response = repairOrderService.getById(id);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('RECEPTIONIST', 'TECHNICIAN', 'MANAGER')")
    public ResponseEntity<ApiResponse<Page<RepairOrderResponse>>> listRepairOrders(
            @RequestParam String garageId,
            @RequestParam(required = false) RepairOrderStatus status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir) {

        Sort sort = sortDir.equalsIgnoreCase(Sort.Direction.ASC.name())
                ? Sort.by(sortBy).ascending()
                : Sort.by(sortBy).descending();
        Pageable pageable = PageRequest.of(page, size, sort);

        Page<RepairOrderResponse> response;
        if (status != null) {
            response = repairOrderService.listByStatus(status, pageable);
        } else {
            response = repairOrderService.listByGarage(garageId, pageable);
        }

        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @PutMapping("/{id}/inspection")
    @PreAuthorize("hasAnyRole('TECHNICIAN', 'MANAGER')")
    public ResponseEntity<ApiResponse<RepairOrderResponse>> updateInspection(
            @PathVariable String id,
            @Valid @RequestBody List<InspectionChecklistDTO> checklist) {
        RepairOrderResponse response = repairOrderService.updateInspection(id, checklist);
        return ResponseEntity.ok(ApiResponse.success(response, "Inspection updated successfully"));
    }

    @PutMapping("/{id}/quote")
    @PreAuthorize("hasAnyRole('RECEPTIONIST', 'MANAGER')")
    public ResponseEntity<ApiResponse<RepairOrderResponse>> updateQuote(
            @PathVariable String id,
            @Valid @RequestBody List<ProposedItemDTO> proposedItems,
            @RequestParam BigDecimal laborCost) {
        RepairOrderResponse response = repairOrderService.updateQuote(id, proposedItems, laborCost);
        return ResponseEntity.ok(ApiResponse.success(response, "Quote updated successfully"));
    }

    @PatchMapping("/{id}/status")
    @PreAuthorize("hasAnyRole('RECEPTIONIST', 'TECHNICIAN', 'MANAGER')")
    public ResponseEntity<ApiResponse<RepairOrderResponse>> updateStatus(
            @PathVariable String id,
            @RequestParam RepairOrderStatus newStatus) {
        RepairOrderResponse response = repairOrderService.updateStatus(id, newStatus);
        return ResponseEntity.ok(ApiResponse.success(response, "Status updated successfully"));
    }

    @PatchMapping("/{id}/approve")
    @PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<ApiResponse<RepairOrderResponse>> approveRepairOrder(
            @PathVariable String id) {
        String customerId = getCurrentUserId();
        RepairOrderResponse response = repairOrderService.customerApprove(id, customerId);
        return ResponseEntity.ok(ApiResponse.success(response, "Repair order approved successfully"));
    }

    private String getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated()) {
            return authentication.getName();
        }
        throw new RuntimeException("User not authenticated");
    }
}
