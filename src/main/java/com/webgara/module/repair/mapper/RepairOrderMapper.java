package com.webgara.module.repair.mapper;

import com.webgara.module.repair.dto.RepairOrderRequest;
import com.webgara.module.repair.dto.RepairOrderResponse;
import com.webgara.module.repair.model.RepairOrder;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface RepairOrderMapper {
    
    RepairOrder toEntity(RepairOrderRequest request);
    
    RepairOrderResponse toResponse(RepairOrder repairOrder);
    
    List<RepairOrderResponse> toResponseList(List<RepairOrder> repairOrders);
}
