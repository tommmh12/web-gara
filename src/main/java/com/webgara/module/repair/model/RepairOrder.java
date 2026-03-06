package com.webgara.module.repair.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.CompoundIndexes;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "repair_orders")
@CompoundIndexes({
        @CompoundIndex(name = "garage_status_idx", def = "{'garageId': 1, 'status': 1}"),
})
public class RepairOrder {

    @Id
    private String id;

    @Indexed(unique = true)
    private String repairOrderNumber;

    @Indexed
    private String appointmentId;

    @Indexed
    private String customerId;

    @Indexed
    private String vehicleId;

    @Indexed
    private String garageId;

    private List<InspectionChecklist> inspectionChecklist;

    private List<ProposedItem> proposedItems;

    private BigDecimal laborCost;

    private BigDecimal totalEstimate;

    private Boolean customerApproved;

    private LocalDateTime approvedAt;

    @Indexed
    private RepairOrderStatus status;

    private LocalDateTime startedAt;

    private LocalDateTime completedAt;

    @CreatedDate
    private LocalDateTime createdAt;

    @LastModifiedDate
    private LocalDateTime updatedAt;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class InspectionChecklist {
        private String item;
        private InspectionCondition condition;
        private String note;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class ProposedItem {
        private ProposedItemType type;
        private String refId;
        private String name;
        private Integer quantity;
        private BigDecimal unitPrice;
        private BigDecimal discount;
        private BigDecimal subtotal;
    }
}
