package com.webgara.module.service.mapper;

import com.webgara.module.service.dto.ServiceItemRequest;
import com.webgara.module.service.dto.ServiceItemResponse;
import com.webgara.module.service.model.ServiceItem;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.ReportingPolicy;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface ServiceItemMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    ServiceItem toEntity(ServiceItemRequest request);

    ServiceItemResponse toResponse(ServiceItem entity);

    List<ServiceItemResponse> toResponseList(List<ServiceItem> entities);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    void updateEntityFromRequest(ServiceItemRequest request, @MappingTarget ServiceItem entity);
}
