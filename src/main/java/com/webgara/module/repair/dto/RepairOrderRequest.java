package com.webgara.module.repair.dto;

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
public class RepairOrderRequest {
    @NotBlank
    private String appointmentId;
    
    private List<InspectionChecklistDTO> inspectionChecklist;
    
    private List<ProposedItemDTO> proposedItems;
    
    @NotNull
    @PositiveOrZero
    private BigDecimal laborCost;
    
    @NotNull
    @PositiveOrZero
    private BigDecimal totalEstimate;
}
