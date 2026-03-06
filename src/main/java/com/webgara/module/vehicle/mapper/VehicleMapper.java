package com.webgara.module.vehicle.mapper;

import com.webgara.module.vehicle.dto.VehicleRequest;
import com.webgara.module.vehicle.dto.VehicleResponse;
import com.webgara.module.vehicle.model.Vehicle;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.ReportingPolicy;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface VehicleMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    Vehicle toEntity(VehicleRequest request);

    VehicleResponse toResponse(Vehicle entity);

    List<VehicleResponse> toResponseList(List<Vehicle> entities);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    // Don't overwrite ownerId or plateNumber on update usually, but depends on biz logic
    @Mapping(target = "ownerId", ignore = true) 
    @Mapping(target = "plateNumber", ignore = true)
    void updateEntityFromRequest(VehicleRequest request, @MappingTarget Vehicle entity);
}
