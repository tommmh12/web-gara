package com.webgara.module.garage.service.impl;

import com.webgara.common.dto.PageResponse;
import com.webgara.common.exception.BadRequestException;
import com.webgara.common.exception.ResourceNotFoundException;
import com.webgara.common.util.SlugUtil;
import com.webgara.module.garage.dto.GarageRequest;
import com.webgara.module.garage.dto.GarageResponse;
import com.webgara.module.garage.mapper.GarageMapper;
import com.webgara.module.garage.model.Garage;
import com.webgara.module.garage.repository.GarageRepository;
import com.webgara.module.garage.service.GarageService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class GarageServiceImpl implements GarageService {

    private final GarageRepository garageRepository;
    private final GarageMapper garageMapper;

    @Override
    @Transactional
    public GarageResponse createGarage(GarageRequest request) {
        String slug = generateUniqueSlug(request.getName());
        
        Garage garage = garageMapper.toEntity(request);
        garage.setSlug(slug);
        
        Garage savedGarage = garageRepository.save(garage);
        return garageMapper.toResponse(savedGarage);
    }

    @Override
    @Transactional
    public GarageResponse updateGarage(String id, GarageRequest request) {
        Garage existingGarage = getGarageEntityById(id);
        
        // If name changes, we might want to update slug, but usually it's better to keep it stable
        // For now, we only update slug if explicitly required, but here we just update other details
        if (!existingGarage.getName().equals(request.getName())) {
             String newSlug = generateUniqueSlug(request.getName());
             existingGarage.setSlug(newSlug);
        }

        garageMapper.updateEntityFromRequest(request, existingGarage);
        
        Garage updatedGarage = garageRepository.save(existingGarage);
        return garageMapper.toResponse(updatedGarage);
    }

    @Override
    public GarageResponse getGarageById(String id) {
        return garageMapper.toResponse(getGarageEntityById(id));
    }

    @Override
    public GarageResponse getGarageBySlug(String slug) {
        Garage garage = garageRepository.findBySlug(slug)
                .orElseThrow(() -> new ResourceNotFoundException("Garage", "slug", slug));
        return garageMapper.toResponse(garage);
    }

    @Override
    public PageResponse<GarageResponse> getAllGarages(Pageable pageable, boolean onlyActive) {
        Page<Garage> garagePage;
        if (onlyActive) {
            garagePage = garageRepository.findByIsActiveTrue(pageable);
        } else {
            garagePage = garageRepository.findAll(pageable);
        }
        
        return PageResponse.<GarageResponse>builder()
                .content(garagePage.getContent().stream().map(garageMapper::toResponse).toList())
                .pageNo(garagePage.getNumber())
                .pageSize(garagePage.getSize())
                .totalElements(garagePage.getTotalElements())
                .totalPages(garagePage.getTotalPages())
                .last(garagePage.isLast())
                .build();
    }

    @Override
    @Transactional
    public void deleteGarage(String id) {
        Garage garage = getGarageEntityById(id);
        // Instead of hard delete, we perform soft delete
        garage.setActive(false);
        garageRepository.save(garage);
        // If hard delete is required: garageRepository.delete(garage);
    }

    private Garage getGarageEntityById(String id) {
        return garageRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Garage", "id", id));
    }

    private String generateUniqueSlug(String name) {
        String baseSlug = SlugUtil.toSlug(name);
        String uniqueSlug = baseSlug;
        int counter = 1;
        while (garageRepository.existsBySlug(uniqueSlug)) {
            uniqueSlug = baseSlug + "-" + counter;
            counter++;
        }
        return uniqueSlug;
    }
}
