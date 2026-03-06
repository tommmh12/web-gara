package com.webgara.module.repair.model;

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

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "repair_tasks")
@CompoundIndexes({
        @CompoundIndex(name = "technician_status_idx", def = "{'technicianId': 1, 'status': 1}"),
})
public class RepairTask {

    @Id
    private String id;

    @Indexed
    private String repairOrderId;

    @Indexed
    private String technicianId;

    private String description;

    private String serviceId;

    private List<PartsUsed> partsUsed;

    @Indexed
    private RepairTaskStatus status;

    private RepairTaskPriority priority;

    private Integer estimatedMinutes;

    private Integer actualMinutes;

    private String notes;

    private LocalDateTime startedAt;

    private LocalDateTime completedAt;

    @CreatedDate
    private LocalDateTime createdAt;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class PartsUsed {
        private String partId;
        private String partName;
        private Integer quantity;
    }
}
