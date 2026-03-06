package com.webgara.module.garage.controller;

import com.webgara.common.dto.ApiResponse;
import com.webgara.common.dto.PageResponse;
import com.webgara.module.garage.dto.GarageRequest;
import com.webgara.module.garage.dto.GarageResponse;
import com.webgara.module.garage.service.GarageService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/garages")
@RequiredArgsConstructor
public class GarageController {

    private final GarageService garageService;

    @PostMapping
    // @PreAuthorize("hasRole('MANAGER')") -- Mocked out for Phase 1
    public ResponseEntity<ApiResponse<GarageResponse>> createGarage(@Valid @RequestBody GarageRequest request) {
        GarageResponse response = garageService.createGarage(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.created(response));
    }

    @PutMapping("/{id}")
    // @PreAuthorize("hasRole('MANAGER')") -- Mocked out for Phase 1
    public ResponseEntity<ApiResponse<GarageResponse>> updateGarage(
            @PathVariable String id,
            @Valid @RequestBody GarageRequest request) {
        GarageResponse response = garageService.updateGarage(id, request);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<GarageResponse>> getGarageById(@PathVariable String id) {
        GarageResponse response = garageService.getGarageById(id);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @GetMapping("/slug/{slug}")
    public ResponseEntity<ApiResponse<GarageResponse>> getGarageBySlug(@PathVariable String slug) {
        GarageResponse response = garageService.getGarageBySlug(slug);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<PageResponse<GarageResponse>>> getAllGarages(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir,
            @RequestParam(defaultValue = "true") boolean onlyActive) {
            
        Sort sort = sortDir.equalsIgnoreCase(Sort.Direction.ASC.name()) ? Sort.by(sortBy).ascending()
                : Sort.by(sortBy).descending();
        Pageable pageable = PageRequest.of(page, size, sort);
        
        PageResponse<GarageResponse> response = garageService.getAllGarages(pageable, onlyActive);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @DeleteMapping("/{id}")
    // @PreAuthorize("hasRole('MANAGER')") -- Mocked out for Phase 1
    public ResponseEntity<ApiResponse<Void>> deleteGarage(@PathVariable String id) {
        garageService.deleteGarage(id);
        return ResponseEntity.ok(ApiResponse.success(null, "Garage deleted successfully"));
    }
}
