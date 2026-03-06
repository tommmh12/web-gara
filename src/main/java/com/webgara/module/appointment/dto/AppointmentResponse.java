package com.webgara.module.appointment.dto;

import com.webgara.module.appointment.model.AppointmentSource;
import com.webgara.module.appointment.model.AppointmentStatus;
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
public class AppointmentResponse {

    private String id;

    private String appointmentNumber;

    private String customerId;

    private String vehicleId;

    private String garageId;

    private List<String> serviceIds;

    private LocalDateTime appointmentDate;

    private TimeSlotDto timeSlot;

    private String description;

    private List<MediaDto> media;

    private AppointmentStatus status;

    private AppointmentSource source;

    private String assignedReceptionistId;

    private EstimatedCostDto estimatedCost;

    private String staffNotes;

    private String cancelReason;

    private List<StatusHistoryEntryDto> statusHistory;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class TimeSlotDto {
        private String start;
        private String end;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class MediaDto {
        private String url;
        private String type;
        private String originalName;
        private LocalDateTime uploadedAt;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class EstimatedCostDto {
        private BigDecimal min;
        private BigDecimal max;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class StatusHistoryEntryDto {
        private AppointmentStatus fromStatus;
        private AppointmentStatus toStatus;
        private String changedBy;
        private LocalDateTime timestamp;
        private String note;
    }
}
