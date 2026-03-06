package com.webgara.module.appointment.model;

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
@Document(collection = "appointments")
@CompoundIndexes({
        @CompoundIndex(name = "garage_date_idx", def = "{'garageId': 1, 'appointmentDate': 1}"),
})
public class Appointment {

    @Id
    private String id;

    @Indexed(unique = true)
    private String appointmentNumber;

    @Indexed
    private String customerId;

    @Indexed
    private String vehicleId;

    @Indexed
    private String garageId;

    private List<String> serviceIds;

    private LocalDateTime appointmentDate;

    private TimeSlot timeSlot;

    private String description;

    private List<Media> media;

    @Indexed
    private AppointmentStatus status;

    @Indexed
    private AppointmentSource source;

    private String assignedReceptionistId;

    private EstimatedCost estimatedCost;

    private String staffNotes;

    private String cancelReason;

    private List<StatusHistoryEntry> statusHistory;

    @CreatedDate
    private LocalDateTime createdAt;

    @LastModifiedDate
    private LocalDateTime updatedAt;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class TimeSlot {
        private String start; // HH:mm format
        private String end;   // HH:mm format
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class Media {
        private String url;
        private MediaType type;
        private String originalName;
        private LocalDateTime uploadedAt;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class StatusHistoryEntry {
        private AppointmentStatus fromStatus;
        private AppointmentStatus toStatus;
        private String changedBy;
        private LocalDateTime timestamp;
        private String note;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class EstimatedCost {
        private BigDecimal min;
        private BigDecimal max;
    }
}
