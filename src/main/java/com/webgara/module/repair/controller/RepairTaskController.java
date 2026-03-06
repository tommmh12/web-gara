package com.webgara.module.repair.controller;

import com.webgara.common.dto.ApiResponse;
import com.webgara.module.repair.dto.RepairTaskRequest;
import com.webgara.module.repair.dto.RepairTaskResponse;
import com.webgara.module.repair.model.RepairTaskStatus;
import com.webgara.module.repair.service.RepairTaskService;
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

import java.util.List;

@RestController
@RequestMapping("/api/v1/repair-tasks")
@RequiredArgsConstructor
public class RepairTaskController {

    private final RepairTaskService repairTaskService;

    @PostMapping
    @PreAuthorize("hasAnyRole('RECEPTIONIST', 'MANAGER')")
    public ResponseEntity<ApiResponse<RepairTaskResponse>> createRepairTask(
            @Valid @RequestBody RepairTaskRequest request) {
        RepairTaskResponse response = repairTaskService.createTask(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.created(response));
    }

    @GetMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<RepairTaskResponse>> getRepairTask(
            @PathVariable String id) {
        RepairTaskResponse response = repairTaskService.getById(id);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @GetMapping("/repair-order/{repairOrderId}")
    @PreAuthorize("hasAnyRole('RECEPTIONIST', 'TECHNICIAN', 'MANAGER')")
    public ResponseEntity<ApiResponse<List<RepairTaskResponse>>> listTasksByRepairOrder(
            @PathVariable String repairOrderId) {
        List<RepairTaskResponse> response = repairTaskService.listTasksByRepairOrder(repairOrderId);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @GetMapping("/technician/{technicianId}")
    @PreAuthorize("hasAnyRole('TECHNICIAN', 'MANAGER')")
    public ResponseEntity<ApiResponse<Page<RepairTaskResponse>>> listTasksByTechnicianAndStatus(
            @PathVariable String technicianId,
            @RequestParam(required = false) RepairTaskStatus status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir) {

        Sort sort = sortDir.equalsIgnoreCase(Sort.Direction.ASC.name())
                ? Sort.by(sortBy).ascending()
                : Sort.by(sortBy).descending();
        Pageable pageable = PageRequest.of(page, size, sort);

        if (status == null) {
            throw new IllegalArgumentException("Status parameter is required");
        }

        Page<RepairTaskResponse> response = repairTaskService.listTasksByTechnicianAndStatus(technicianId, status, pageable);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @PatchMapping("/{taskId}/status")
    @PreAuthorize("hasAnyRole('TECHNICIAN', 'MANAGER')")
    public ResponseEntity<ApiResponse<RepairTaskResponse>> updateTaskStatus(
            @PathVariable String taskId,
            @RequestParam RepairTaskStatus newStatus) {
        String technicianId = getCurrentUserId();
        RepairTaskResponse response = repairTaskService.updateTaskStatus(taskId, newStatus, technicianId);
        return ResponseEntity.ok(ApiResponse.success(response, "Task status updated successfully"));
    }

    @PatchMapping("/{taskId}/progress")
    @PreAuthorize("hasAnyRole('TECHNICIAN', 'MANAGER')")
    public ResponseEntity<ApiResponse<RepairTaskResponse>> updateTaskProgress(
            @PathVariable String taskId,
            @RequestParam Integer actualMinutes,
            @RequestParam(required = false) String notes) {
        RepairTaskResponse response = repairTaskService.updateTaskProgress(taskId, actualMinutes, notes);
        return ResponseEntity.ok(ApiResponse.success(response, "Task progress updated successfully"));
    }

    private String getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated()) {
            return authentication.getName();
        }
        throw new RuntimeException("User not authenticated");
    }
}
