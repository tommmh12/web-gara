package com.webgara.module.vehicle.controller;

import com.webgara.common.dto.ApiResponse;
import com.webgara.common.dto.PageResponse;
import com.webgara.module.vehicle.dto.VehicleRequest;
import com.webgara.module.vehicle.dto.VehicleResponse;
import com.webgara.module.vehicle.service.VehicleService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/vehicles")
@RequiredArgsConstructor
public class VehicleController {

    private final VehicleService vehicleService;

    @PostMapping
    public ResponseEntity<ApiResponse<VehicleResponse>> createVehicle(
            // @AuthenticationPrincipal UserDetailsImpl userDetails,
            @RequestHeader(value = "X-Mock-User-Id", required = false) String mockUserId,
            @Valid @RequestBody VehicleRequest request) {
        
        // Use Mock UserId if Auth module is not ready yet
        String ownerId = mockUserId != null ? mockUserId : request.getOwnerId();
            
        VehicleResponse response = vehicleService.createVehicle(request, ownerId);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.created(response));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<VehicleResponse>> updateVehicle(
            @PathVariable String id,
            @Valid @RequestBody VehicleRequest request) {
        VehicleResponse response = vehicleService.updateVehicle(id, request);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<VehicleResponse>> getVehicleById(@PathVariable String id) {
        VehicleResponse response = vehicleService.getVehicleById(id);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<PageResponse<VehicleResponse>>> getMyVehicles(
            // @AuthenticationPrincipal UserDetailsImpl userDetails,
            @RequestHeader(value = "X-Mock-User-Id", required = true) String mockUserId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir) {

        Sort sort = sortDir.equalsIgnoreCase(Sort.Direction.ASC.name()) ? Sort.by(sortBy).ascending()
                : Sort.by(sortBy).descending();
        Pageable pageable = PageRequest.of(page, size, sort);

        PageResponse<VehicleResponse> response = vehicleService.getMyVehicles(mockUserId, pageable);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteVehicle(@PathVariable String id) {
        vehicleService.deleteVehicle(id);
        return ResponseEntity.ok(ApiResponse.success(null, "Vehicle deleted successfully"));
    }
    
    @PutMapping("/{id}/mileage")
    public ResponseEntity<ApiResponse<VehicleResponse>> updateMileage(
            @PathVariable String id,
            @RequestParam Integer mileage) {
        VehicleResponse response = vehicleService.updateMileage(id, mileage);
        return ResponseEntity.ok(ApiResponse.success(response));
    }
}
