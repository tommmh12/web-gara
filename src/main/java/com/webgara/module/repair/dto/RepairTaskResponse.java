package com.webgara.module.repair.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RepairTaskResponse {
    private String id;
    private String repairOrderId;
    private String technicianId;
    private String description;
    private String serviceId;
    private List<PartsUsedDTO> partsUsed;
    private String status;
    private String priority;
    private Integer estimatedMinutes;
    private Integer actualMinutes;
    private String notes;
    private LocalDateTime startedAt;
    private LocalDateTime completedAt;
    private LocalDateTime createdAt;
}
