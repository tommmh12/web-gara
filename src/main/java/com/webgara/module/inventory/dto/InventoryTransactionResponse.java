package com.webgara.module.inventory.dto;

import com.webgara.module.inventory.model.ReferenceType;
import com.webgara.module.inventory.model.TransactionType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InventoryTransactionResponse {
    private String id;
    private String partId;
    private String garageId;
    private TransactionType type;
    private Integer quantity;
    private Integer previousQuantity;
    private Integer newQuantity;
    private String reason;
    private BigDecimal unitPrice;
    private ReferenceType referenceType;
    private String referenceId;
    private String performedBy;
    private LocalDateTime createdAt;
}
