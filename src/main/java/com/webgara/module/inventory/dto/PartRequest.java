package com.webgara.module.inventory.dto;

import com.webgara.module.inventory.model.PartCategory;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PartRequest {
    private String code;

    @NotBlank(message = "Part name is required")
    private String name;

    private String description;

    @NotNull(message = "Category is required")
    private PartCategory category;

    private String brand;

    private CompatibleVehiclesDTO compatibleVehicles;

    @NotNull(message = "Quantity is required")
    @PositiveOrZero(message = "Quantity must be >= 0")
    private Integer quantity;

    @NotNull(message = "Minimum quantity is required")
    @PositiveOrZero(message = "Minimum quantity must be >= 0")
    private Integer minQuantity;

    @NotBlank(message = "Unit is required")
    private String unit;

    @NotNull(message = "Import price is required")
    @PositiveOrZero(message = "Import price must be >= 0")
    private BigDecimal importPrice;

    @NotNull(message = "Sell price is required")
    @PositiveOrZero(message = "Sell price must be >= 0")
    private BigDecimal sellPrice;

    @Valid
    private SupplierDTO supplier;

    private String location;

    @NotBlank(message = "Garage ID is required")
    private String garageId;

    @Builder.Default
    private Boolean isActive = true;
}
