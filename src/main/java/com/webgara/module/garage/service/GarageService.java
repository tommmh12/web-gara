package com.webgara.module.garage.service;

import com.webgara.common.dto.PageResponse;
import com.webgara.module.garage.dto.GarageRequest;
import com.webgara.module.garage.dto.GarageResponse;
import org.springframework.data.domain.Pageable;

public interface GarageService {
    GarageResponse createGarage(GarageRequest request);
    
    GarageResponse updateGarage(String id, GarageRequest request);
    
    GarageResponse getGarageById(String id);
    
    GarageResponse getGarageBySlug(String slug);
    
    PageResponse<GarageResponse> getAllGarages(Pageable pageable, boolean onlyActive);
    
    void deleteGarage(String id);
    
    // Optional: Get availability logic could go here or in a separate Availability Service
}
