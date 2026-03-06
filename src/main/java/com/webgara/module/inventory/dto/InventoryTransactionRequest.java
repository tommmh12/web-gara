package com.webgara.module.inventory.dto;

import com.webgara.module.inventory.model.ReferenceType;
import com.webgara.module.inventory.model.TransactionType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InventoryTransactionRequest {
    @NotBlank(message = "Part ID is required")
    private String partId;

    @NotBlank(message = "Garage ID is required")
    private String garageId;

    @NotNull(message = "Transaction type is required")
    private TransactionType type;

    @NotNull(message = "Quantity is required")
    private Integer quantity;

    @NotBlank(message = "Reason is required")
    private String reason;

    @PositiveOrZero(message = "Unit price must be >= 0")
    private BigDecimal unitPrice;

    private ReferenceType referenceType;

    private String referenceId;
}
