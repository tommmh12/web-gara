package com.webgara.module.vehicle.service;

import com.webgara.common.dto.PageResponse;
import com.webgara.module.vehicle.dto.VehicleRequest;
import com.webgara.module.vehicle.dto.VehicleResponse;
import org.springframework.data.domain.Pageable;

public interface VehicleService {
    VehicleResponse createVehicle(VehicleRequest request, String ownerId);
    
    VehicleResponse updateVehicle(String id, VehicleRequest request);
    
    VehicleResponse getVehicleById(String id);
    
    VehicleResponse getVehicleByPlateNumber(String plateNumber);
    
    PageResponse<VehicleResponse> getMyVehicles(String ownerId, Pageable pageable);
    
    void deleteVehicle(String id);
    
    VehicleResponse updateMileage(String id, Integer newMileage);
}
