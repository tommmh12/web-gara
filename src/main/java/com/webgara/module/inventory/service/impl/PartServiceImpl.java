package com.webgara.module.inventory.service.impl;

import com.webgara.common.exception.BadRequestException;
import com.webgara.common.exception.ResourceNotFoundException;
import com.webgara.module.inventory.dto.PartRequest;
import com.webgara.module.inventory.dto.PartResponse;
import com.webgara.module.inventory.mapper.PartMapper;
import com.webgara.module.inventory.model.Part;
import com.webgara.module.inventory.model.PartCategory;
import com.webgara.module.inventory.repository.PartRepository;
import com.webgara.module.inventory.service.PartService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PartServiceImpl implements PartService {

    private final PartRepository partRepository;
    private final PartMapper partMapper;

    @Override
    @Transactional
    public PartResponse create(PartRequest request) {
        if (request.getCode() == null || request.getCode().isBlank()) {
            request.setCode(generatePartCode());
        } else {
            if (partRepository.findByCode(request.getCode()).isPresent()) {
                throw new BadRequestException("Part code already exists: " + request.getCode());
            }
        }

        Part part = partMapper.toEntity(request);
        Part saved = partRepository.save(part);
        return partMapper.toResponse(saved);
    }

    @Override
    public PartResponse getById(String id) {
        Part part = partRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Part", "id", id));
        return partMapper.toResponse(part);
    }

    @Override
    @Transactional
    public PartResponse update(String id, PartRequest request) {
        Part part = partRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Part", "id", id));

        if (request.getCode() != null && !request.getCode().equals(part.getCode())) {
            if (partRepository.findByCode(request.getCode()).isPresent()) {
                throw new BadRequestException("Part code already exists: " + request.getCode());
            }
            part.setCode(request.getCode());
        }

        part.setName(request.getName());
        part.setDescription(request.getDescription());
        part.setCategory(request.getCategory());
        part.setBrand(request.getBrand());
        part.setCompatibleVehicles(request.getCompatibleVehicles() != null ? 
                new Part.CompatibleVehicles(request.getCompatibleVehicles().getBrand(), 
                        request.getCompatibleVehicles().getModels()) : null);
        part.setQuantity(request.getQuantity());
        part.setMinQuantity(request.getMinQuantity());
        part.setUnit(request.getUnit());
        part.setImportPrice(request.getImportPrice());
        part.setSellPrice(request.getSellPrice());
        part.setSupplier(request.getSupplier() != null ?
                new Part.Supplier(request.getSupplier().getName(),
                        request.getSupplier().getPhone(),
                        request.getSupplier().getEmail()) : null);
        part.setLocation(request.getLocation());
        part.setIsActive(request.getIsActive());

        Part saved = partRepository.save(part);
        return partMapper.toResponse(saved);
    }

    @Override
    @Transactional
    public void delete(String id) {
        Part part = partRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Part", "id", id));
        part.setIsActive(false);
        partRepository.save(part);
    }

    @Override
    public Page<PartResponse> listByGarage(String garageId, Pageable pageable) {
        Page<Part> page = partRepository.findByGarageId(garageId, pageable);
        return page.map(partMapper::toResponse);
    }

    @Override
    public Page<PartResponse> listByCategory(String garageId, PartCategory category, Pageable pageable) {
        Page<Part> page = partRepository.findByGarageIdAndCategory(garageId, category, pageable);
        return page.map(partMapper::toResponse);
    }

    @Override
    public Page<PartResponse> searchByName(String name, Pageable pageable) {
        Page<Part> page = partRepository.findByNameContainingIgnoreCase(name, pageable);
        return page.map(partMapper::toResponse);
    }

    @Override
    public List<PartResponse> getLowStockParts(String garageId) {
        List<Part> parts = partRepository.findByGarageIdAndQuantityLessThanEqualMinQuantity(garageId);
        return partMapper.toResponseList(parts);
    }

    private String generatePartCode() {
        List<Part> allParts = partRepository.findAll();
        int maxCode = 0;

        for (Part part : allParts) {
            if (part.getCode() != null && part.getCode().startsWith("PT-")) {
                try {
                    int code = Integer.parseInt(part.getCode().substring(3));
                    if (code > maxCode) {
                        maxCode = code;
                    }
                } catch (NumberFormatException e) {
                    // Ignore non-numeric codes
                }
            }
        }

        int nextCode = maxCode + 1;
        return String.format("PT-%03d", nextCode);
    }
}
