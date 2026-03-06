package com.webgara.module.inventory.dto;

import com.webgara.module.inventory.model.PartCategory;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PartResponse {
    private String id;
    private String code;
    private String name;
    private String description;
    private PartCategory category;
    private String brand;
    private CompatibleVehiclesDTO compatibleVehicles;
    private Integer quantity;
    private Integer minQuantity;
    private String unit;
    private BigDecimal importPrice;
    private BigDecimal sellPrice;
    private SupplierDTO supplier;
    private String location;
    private String garageId;
    private Boolean isActive;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
