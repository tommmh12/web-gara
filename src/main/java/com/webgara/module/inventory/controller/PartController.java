package com.webgara.module.inventory.controller;

import com.webgara.common.dto.ApiResponse;
import com.webgara.module.inventory.dto.PartRequest;
import com.webgara.module.inventory.dto.PartResponse;
import com.webgara.module.inventory.model.PartCategory;
import com.webgara.module.inventory.service.PartService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/parts")
@RequiredArgsConstructor
public class PartController {

    private final PartService partService;

    @PostMapping
    @PreAuthorize("hasAnyRole('MANAGER')")
    public ResponseEntity<ApiResponse<PartResponse>> createPart(
            @Valid @RequestBody PartRequest request) {
        PartResponse response = partService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.created(response));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('RECEPTIONIST', 'TECHNICIAN', 'MANAGER')")
    public ResponseEntity<ApiResponse<PartResponse>> getPart(
            @PathVariable String id) {
        PartResponse response = partService.getById(id);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('MANAGER')")
    public ResponseEntity<ApiResponse<PartResponse>> updatePart(
            @PathVariable String id,
            @Valid @RequestBody PartRequest request) {
        PartResponse response = partService.update(id, request);
        return ResponseEntity.ok(ApiResponse.success(response, "Part updated successfully"));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('MANAGER')")
    public ResponseEntity<ApiResponse<Void>> deletePart(
            @PathVariable String id) {
        partService.delete(id);
        return ResponseEntity.ok(ApiResponse.success(null, "Part deleted successfully"));
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('RECEPTIONIST', 'TECHNICIAN', 'MANAGER')")
    public ResponseEntity<ApiResponse<Page<PartResponse>>> listParts(
            @RequestParam String garageId,
            @RequestParam(required = false) PartCategory category,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir) {

        Sort sort = sortDir.equalsIgnoreCase(Sort.Direction.ASC.name())
                ? Sort.by(sortBy).ascending()
                : Sort.by(sortBy).descending();
        Pageable pageable = PageRequest.of(page, size, sort);

        Page<PartResponse> response;
        if (category != null) {
            response = partService.listByCategory(garageId, category, pageable);
        } else {
            response = partService.listByGarage(garageId, pageable);
        }

        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @GetMapping("/search")
    @PreAuthorize("hasAnyRole('RECEPTIONIST', 'TECHNICIAN', 'MANAGER')")
    public ResponseEntity<ApiResponse<Page<PartResponse>>> searchParts(
            @RequestParam String name,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir) {

        Sort sort = sortDir.equalsIgnoreCase(Sort.Direction.ASC.name())
                ? Sort.by(sortBy).ascending()
                : Sort.by(sortBy).descending();
        Pageable pageable = PageRequest.of(page, size, sort);

        Page<PartResponse> response = partService.searchByName(name, pageable);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @GetMapping("/low-stock")
    @PreAuthorize("hasAnyRole('RECEPTIONIST', 'MANAGER')")
    public ResponseEntity<ApiResponse<List<PartResponse>>> getLowStockParts(
            @RequestParam String garageId) {
        List<PartResponse> response = partService.getLowStockParts(garageId);
        return ResponseEntity.ok(ApiResponse.success(response));
    }
}
