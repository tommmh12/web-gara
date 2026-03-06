package com.webgara.module.service.service.impl;

import com.webgara.common.dto.PageResponse;
import com.webgara.common.exception.ResourceNotFoundException;
import com.webgara.module.garage.repository.GarageRepository;
import com.webgara.module.service.dto.ServiceItemRequest;
import com.webgara.module.service.dto.ServiceItemResponse;
import com.webgara.module.service.mapper.ServiceItemMapper;
import com.webgara.module.service.model.ServiceItem;
import com.webgara.module.service.repository.ServiceItemRepository;
import com.webgara.module.service.service.ServiceCatalogService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ServiceCatalogServiceImpl implements ServiceCatalogService {

    private final ServiceItemRepository serviceItemRepository;
    private final ServiceItemMapper serviceItemMapper;
    private final GarageRepository garageRepository;

    @Override
    @Transactional
    public ServiceItemResponse createService(ServiceItemRequest request) {
        // Validate garage exists
        if (!garageRepository.existsById(request.getGarageId())) {
            throw new ResourceNotFoundException("Garage", "id", request.getGarageId());
        }

        ServiceItem serviceItem = serviceItemMapper.toEntity(request);
        ServiceItem savedService = serviceItemRepository.save(serviceItem);
        return serviceItemMapper.toResponse(savedService);
    }

    @Override
    @Transactional
    public ServiceItemResponse updateService(String id, ServiceItemRequest request) {
        ServiceItem existingService = getServiceEntityById(id);
        
        // If garageId changes, validate new garage exists
        if (!existingService.getGarageId().equals(request.getGarageId()) && 
            !garageRepository.existsById(request.getGarageId())) {
            throw new ResourceNotFoundException("Garage", "id", request.getGarageId());
        }

        serviceItemMapper.updateEntityFromRequest(request, existingService);
        ServiceItem updatedService = serviceItemRepository.save(existingService);
        return serviceItemMapper.toResponse(updatedService);
    }

    @Override
    public ServiceItemResponse getServiceById(String id) {
        return serviceItemMapper.toResponse(getServiceEntityById(id));
    }

    @Override
    public PageResponse<ServiceItemResponse> getServicesByGarageId(String garageId, Pageable pageable, boolean onlyActive) {
        Page<ServiceItem> servicePage;
        if (onlyActive) {
            servicePage = serviceItemRepository.findByGarageIdAndIsActiveTrue(garageId, pageable);
        } else {
            servicePage = serviceItemRepository.findByGarageId(garageId, pageable);
        }

        return PageResponse.<ServiceItemResponse>builder()
                .content(servicePage.getContent().stream().map(serviceItemMapper::toResponse).toList())
                .pageNo(servicePage.getNumber())
                .pageSize(servicePage.getSize())
                .totalElements(servicePage.getTotalElements())
                .totalPages(servicePage.getTotalPages())
                .last(servicePage.isLast())
                .build();
    }

    @Override
    public PageResponse<ServiceItemResponse> getServicesByCategory(String category, Pageable pageable) {
        Page<ServiceItem> servicePage = serviceItemRepository.findByCategoryAndIsActiveTrue(category, pageable);
        
        return PageResponse.<ServiceItemResponse>builder()
                .content(servicePage.getContent().stream().map(serviceItemMapper::toResponse).toList())
                .pageNo(servicePage.getNumber())
                .pageSize(servicePage.getSize())
                .totalElements(servicePage.getTotalElements())
                .totalPages(servicePage.getTotalPages())
                .last(servicePage.isLast())
                .build();
    }

    @Override
    @Transactional
    public void deleteService(String id) {
        ServiceItem existingService = getServiceEntityById(id);
        existingService.setActive(false); // soft delete
        serviceItemRepository.save(existingService);
    }

    private ServiceItem getServiceEntityById(String id) {
        return serviceItemRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("ServiceItem", "id", id));
    }
}
