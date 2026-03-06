package com.webgara.module.repair.dto;

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
public class RepairOrderResponse {
    private String id;
    private String repairOrderNumber;
    private String appointmentId;
    private String customerId;
    private String vehicleId;
    private String garageId;
    private List<InspectionChecklistDTO> inspectionChecklist;
    private List<ProposedItemDTO> proposedItems;
    private BigDecimal laborCost;
    private BigDecimal totalEstimate;
    private Boolean customerApproved;
    private LocalDateTime approvedAt;
    private String status;
    private LocalDateTime startedAt;
    private LocalDateTime completedAt;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
