package com.webgara.module.vehicle.service.impl;

import com.webgara.common.dto.PageResponse;
import com.webgara.common.exception.BadRequestException;
import com.webgara.common.exception.ConflictException;
import com.webgara.common.exception.ResourceNotFoundException;
import com.webgara.module.vehicle.dto.VehicleRequest;
import com.webgara.module.vehicle.dto.VehicleResponse;
import com.webgara.module.vehicle.mapper.VehicleMapper;
import com.webgara.module.vehicle.model.Vehicle;
import com.webgara.module.vehicle.repository.VehicleRepository;
import com.webgara.module.vehicle.service.VehicleService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class VehicleServiceImpl implements VehicleService {

    private final VehicleRepository vehicleRepository;
    private final VehicleMapper vehicleMapper;

    @Override
    @Transactional
    public VehicleResponse createVehicle(VehicleRequest request, String ownerId) {
        if (vehicleRepository.existsByPlateNumber(request.getPlateNumber())) {
            throw new ConflictException("Vehicle with plate number " + request.getPlateNumber() + " already exists.");
        }

        Vehicle vehicle = vehicleMapper.toEntity(request);
        // Explicitly set the owner ID from the authenticated context usually
        vehicle.setOwnerId(ownerId != null ? ownerId : request.getOwnerId());
        
        Vehicle savedVehicle = vehicleRepository.save(vehicle);
        return vehicleMapper.toResponse(savedVehicle);
    }

    @Override
    @Transactional
    public VehicleResponse updateVehicle(String id, VehicleRequest request) {
        Vehicle existingVehicle = getVehicleEntityById(id);
        
        vehicleMapper.updateEntityFromRequest(request, existingVehicle);
        
        Vehicle updatedVehicle = vehicleRepository.save(existingVehicle);
        return vehicleMapper.toResponse(updatedVehicle);
    }

    @Override
    public VehicleResponse getVehicleById(String id) {
        return vehicleMapper.toResponse(getVehicleEntityById(id));
    }

    @Override
    public VehicleResponse getVehicleByPlateNumber(String plateNumber) {
        Vehicle vehicle = vehicleRepository.findByPlateNumber(plateNumber)
                .orElseThrow(() -> new ResourceNotFoundException("Vehicle", "plateNumber", plateNumber));
        return vehicleMapper.toResponse(vehicle);
    }

    @Override
    public PageResponse<VehicleResponse> getMyVehicles(String ownerId, Pageable pageable) {
        Page<Vehicle> vehiclePage = vehicleRepository.findByOwnerId(ownerId, pageable);
        
        return PageResponse.<VehicleResponse>builder()
                .content(vehiclePage.getContent().stream().map(vehicleMapper::toResponse).toList())
                .pageNo(vehiclePage.getNumber())
                .pageSize(vehiclePage.getSize())
                .totalElements(vehiclePage.getTotalElements())
                .totalPages(vehiclePage.getTotalPages())
                .last(vehiclePage.isLast())
                .build();
    }

    @Override
    @Transactional
    public void deleteVehicle(String id) {
        Vehicle existingVehicle = getVehicleEntityById(id);
        vehicleRepository.delete(existingVehicle);
    }

    @Override
    @Transactional
    public VehicleResponse updateMileage(String id, Integer newMileage) {
        Vehicle existingVehicle = getVehicleEntityById(id);
        if (newMileage < existingVehicle.getMileage()) {
             throw new BadRequestException("New mileage cannot be less than current mileage");
        }
        existingVehicle.setMileage(newMileage);
        Vehicle updatedVehicle = vehicleRepository.save(existingVehicle);
        return vehicleMapper.toResponse(updatedVehicle);
    }

    private Vehicle getVehicleEntityById(String id) {
        return vehicleRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Vehicle", "id", id));
    }
}
