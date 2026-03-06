package com.webgara.module.garage.mapper;

import com.webgara.module.garage.dto.GarageRequest;
import com.webgara.module.garage.dto.GarageResponse;
import com.webgara.module.garage.model.Garage;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.ReportingPolicy;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface GarageMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "slug", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    Garage toEntity(GarageRequest request);

    GarageResponse toResponse(Garage entity);

    List<GarageResponse> toResponseList(List<Garage> entities);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "slug", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    void updateEntityFromRequest(GarageRequest request, @MappingTarget Garage entity);
}
