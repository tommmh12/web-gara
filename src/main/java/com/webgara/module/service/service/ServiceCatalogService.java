package com.webgara.module.service.service;

import com.webgara.common.dto.PageResponse;
import com.webgara.module.service.dto.ServiceItemRequest;
import com.webgara.module.service.dto.ServiceItemResponse;
import org.springframework.data.domain.Pageable;

public interface ServiceCatalogService {
    ServiceItemResponse createService(ServiceItemRequest request);
    
    ServiceItemResponse updateService(String id, ServiceItemRequest request);
    
    ServiceItemResponse getServiceById(String id);
    
    PageResponse<ServiceItemResponse> getServicesByGarageId(String garageId, Pageable pageable, boolean onlyActive);
    
    PageResponse<ServiceItemResponse> getServicesByCategory(String category, Pageable pageable);
    
    void deleteService(String id);
    
    // Estimate calculation could go here later
}
