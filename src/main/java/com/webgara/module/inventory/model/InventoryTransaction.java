package com.webgara.module.inventory.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.CompoundIndexes;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "inventory_transactions")
@CompoundIndexes({
        @CompoundIndex(name = "part_createdAt_idx", def = "{'partId': 1, 'createdAt': -1}"),
        @CompoundIndex(name = "refType_refId_idx", def = "{'referenceType': 1, 'referenceId': 1}"),
})
public class InventoryTransaction {

    @Id
    private String id;

    @Indexed
    private String partId;

    @Indexed
    private String garageId;

    @Indexed
    private TransactionType type;

    private Integer quantity;

    private Integer previousQuantity;

    private Integer newQuantity;

    private String reason;

    private BigDecimal unitPrice;

    private ReferenceType referenceType;

    private String referenceId;

    private String performedBy;

    @CreatedDate
    private LocalDateTime createdAt;
}
