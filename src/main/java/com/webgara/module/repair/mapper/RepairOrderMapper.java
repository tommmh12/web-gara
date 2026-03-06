package com.webgara.module.repair.mapper;

import com.webgara.module.repair.dto.RepairOrderRequest;
import com.webgara.module.repair.dto.RepairOrderResponse;
import com.webgara.module.repair.model.RepairOrder;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface RepairOrderMapper {
    
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "repairOrderNumber", ignore = true)
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    RepairOrder toEntity(RepairOrderRequest request);
    
    RepairOrderResponse toResponse(RepairOrder repairOrder);
    
    List<RepairOrderResponse> toResponseList(List<RepairOrder> repairOrders);
}
