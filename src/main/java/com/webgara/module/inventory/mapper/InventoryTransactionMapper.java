package com.webgara.module.inventory.mapper;

import com.webgara.module.inventory.dto.InventoryTransactionRequest;
import com.webgara.module.inventory.dto.InventoryTransactionResponse;
import com.webgara.module.inventory.model.InventoryTransaction;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface InventoryTransactionMapper {
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    InventoryTransaction toEntity(InventoryTransactionRequest request);

    InventoryTransactionResponse toResponse(InventoryTransaction transaction);

    List<InventoryTransactionResponse> toResponseList(List<InventoryTransaction> transactions);
}
