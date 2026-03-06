package com.webgara.module.appointment.dto;

import com.webgara.module.appointment.model.AppointmentSource;
import com.webgara.module.appointment.model.AppointmentStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AppointmentRequest {

    @NotNull(message = "Vehicle ID is required")
    private String vehicleId;

    @NotNull(message = "Garage ID is required")
    private String garageId;

    @NotEmpty(message = "At least one service must be selected")
    private List<String> serviceIds;

    @NotNull(message = "Appointment date is required")
    @Future(message = "Appointment date must be in the future")
    private LocalDateTime appointmentDate;

    @Valid
    @NotNull(message = "Time slot is required")
    private TimeSlotDto timeSlot;

    @NotBlank(message = "Description is required")
    private String description;

    @NotNull(message = "Source is required")
    private AppointmentSource source;

    private List<MediaDto> media;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class TimeSlotDto {
        @NotBlank(message = "Start time is required")
        private String start; // HH:mm

        @NotBlank(message = "End time is required")
        private String end;   // HH:mm
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class MediaDto {
        private String url;
        private String type;
        private String originalName;
    }
}
