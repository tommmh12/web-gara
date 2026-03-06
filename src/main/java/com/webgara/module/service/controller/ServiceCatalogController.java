package com.webgara.module.service.controller;

import com.webgara.common.dto.ApiResponse;
import com.webgara.common.dto.PageResponse;
import com.webgara.module.service.dto.ServiceItemRequest;
import com.webgara.module.service.dto.ServiceItemResponse;
import com.webgara.module.service.service.ServiceCatalogService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/services")
@RequiredArgsConstructor
public class ServiceCatalogController {

    private final ServiceCatalogService serviceCatalogService;

    @PostMapping
    // @PreAuthorize("hasRole('MANAGER')") -- Mocked for Phase 1
    public ResponseEntity<ApiResponse<ServiceItemResponse>> createService(@Valid @RequestBody ServiceItemRequest request) {
        ServiceItemResponse response = serviceCatalogService.createService(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.created(response));
    }

    @PutMapping("/{id}")
    // @PreAuthorize("hasRole('MANAGER')") -- Mocked for Phase 1
    public ResponseEntity<ApiResponse<ServiceItemResponse>> updateService(
            @PathVariable String id,
            @Valid @RequestBody ServiceItemRequest request) {
        ServiceItemResponse response = serviceCatalogService.updateService(id, request);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<ServiceItemResponse>> getServiceById(@PathVariable String id) {
        ServiceItemResponse response = serviceCatalogService.getServiceById(id);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @GetMapping("/garage/{garageId}")
    public ResponseEntity<ApiResponse<PageResponse<ServiceItemResponse>>> getServicesByGarageId(
            @PathVariable String garageId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir,
            @RequestParam(defaultValue = "true") boolean onlyActive) {

        Sort sort = sortDir.equalsIgnoreCase(Sort.Direction.ASC.name()) ? Sort.by(sortBy).ascending()
                : Sort.by(sortBy).descending();
        Pageable pageable = PageRequest.of(page, size, sort);

        PageResponse<ServiceItemResponse> response = serviceCatalogService.getServicesByGarageId(garageId, pageable, onlyActive);
        return ResponseEntity.ok(ApiResponse.success(response));
    }
    
    @GetMapping("/category/{category}")
    public ResponseEntity<ApiResponse<PageResponse<ServiceItemResponse>>> getServicesByCategory(
            @PathVariable String category,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir) {

        Sort sort = sortDir.equalsIgnoreCase(Sort.Direction.ASC.name()) ? Sort.by(sortBy).ascending()
                : Sort.by(sortBy).descending();
        Pageable pageable = PageRequest.of(page, size, sort);

        PageResponse<ServiceItemResponse> response = serviceCatalogService.getServicesByCategory(category, pageable);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @DeleteMapping("/{id}")
    // @PreAuthorize("hasRole('MANAGER')") -- Mocked for Phase 1
    public ResponseEntity<ApiResponse<Void>> deleteService(@PathVariable String id) {
        serviceCatalogService.deleteService(id);
        return ResponseEntity.ok(ApiResponse.success(null, "Service deleted successfully"));
    }
}
