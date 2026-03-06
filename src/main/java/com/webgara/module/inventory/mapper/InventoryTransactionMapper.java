package com.webgara.module.inventory.mapper;

import com.webgara.module.inventory.dto.InventoryTransactionRequest;
import com.webgara.module.inventory.dto.InventoryTransactionResponse;
import com.webgara.module.inventory.model.InventoryTransaction;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface InventoryTransactionMapper {
    InventoryTransaction toEntity(InventoryTransactionRequest request);

    InventoryTransactionResponse toResponse(InventoryTransaction transaction);

    List<InventoryTransactionResponse> toResponseList(List<InventoryTransaction> transactions);
}
