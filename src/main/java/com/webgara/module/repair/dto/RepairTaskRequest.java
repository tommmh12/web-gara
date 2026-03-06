package com.webgara.module.repair.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RepairTaskRequest {
    @NotBlank
    private String repairOrderId;
    
    @NotBlank
    private String technicianId;
    
    @NotBlank
    private String description;
    
    private String serviceId;
    
    private List<PartsUsedDTO> partsUsed;
    
    @NotNull
    private String priority;
    
    @NotNull
    @Positive
    private Integer estimatedMinutes;
}
