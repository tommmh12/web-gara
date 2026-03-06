package com.webgara.module.appointment.service.impl;

import com.webgara.common.dto.PageResponse;
import com.webgara.common.exception.BadRequestException;
import com.webgara.common.exception.ResourceNotFoundException;
import com.webgara.module.appointment.dto.AppointmentRequest;
import com.webgara.module.appointment.dto.AppointmentResponse;
import com.webgara.module.appointment.mapper.AppointmentMapper;
import com.webgara.module.appointment.model.Appointment;
import com.webgara.module.appointment.model.AppointmentStatus;
import com.webgara.module.appointment.repository.AppointmentRepository;
import com.webgara.module.appointment.service.AppointmentService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AppointmentServiceImpl implements AppointmentService {

    private final AppointmentRepository appointmentRepository;
    private final AppointmentMapper appointmentMapper;

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyyMMdd");
    private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm");

    @Override
    @Transactional
    public AppointmentResponse createAppointment(String customerId, AppointmentRequest request) {
        // Check for time slot conflicts
        String dateStr = request.getAppointmentDate().toLocalDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        if (hasTimeSlotConflict(request.getGarageId(), dateStr, request.getTimeSlot().getStart(), request.getTimeSlot().getEnd())) {
            throw new BadRequestException("Time slot is not available for the selected date and garage");
        }

        // Convert DTO to entity
        Appointment appointment = appointmentMapper.toEntity(request);
        appointment.setCustomerId(customerId);
        appointment.setStatus(AppointmentStatus.PENDING_CONFIRMATION);
        appointment.setAppointmentNumber(generateAppointmentNumber(request.getGarageId(), request.getAppointmentDate()));

        // Initialize status history
        List<Appointment.StatusHistoryEntry> statusHistory = new ArrayList<>();
        statusHistory.add(Appointment.StatusHistoryEntry.builder()
                .fromStatus(null)
                .toStatus(AppointmentStatus.PENDING_CONFIRMATION)
                .changedBy(customerId)
                .timestamp(LocalDateTime.now())
                .note("Appointment created")
                .build());
        appointment.setStatusHistory(statusHistory);

        Appointment savedAppointment = appointmentRepository.save(appointment);
        return appointmentMapper.toResponse(savedAppointment);
    }

    @Override
    public AppointmentResponse getAppointmentById(String id) {
        Appointment appointment = appointmentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Appointment", "id", id));
        return appointmentMapper.toResponse(appointment);
    }

    @Override
    public PageResponse<AppointmentResponse> getMyAppointments(String customerId, Pageable pageable) {
        Page<Appointment> page = appointmentRepository.findByCustomerId(customerId, pageable);
        return mapPageToPageResponse(page);
    }

    @Override
    @Transactional
    public AppointmentResponse cancelAppointment(String id, String cancelReason) {
        Appointment appointment = appointmentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Appointment", "id", id));

        if (appointment.getStatus() == AppointmentStatus.CANCELLED) {
            throw new BadRequestException("Appointment is already cancelled");
        }

        // Only allow cancellation from certain statuses
        if (appointment.getStatus() == AppointmentStatus.DELIVERED || appointment.getStatus() == AppointmentStatus.COMPLETED) {
            throw new BadRequestException("Cannot cancel appointment in " + appointment.getStatus() + " status");
        }

        AppointmentStatus oldStatus = appointment.getStatus();
        appointment.setStatus(AppointmentStatus.CANCELLED);
        appointment.setCancelReason(cancelReason);

        // Add to status history
        if (appointment.getStatusHistory() == null) {
            appointment.setStatusHistory(new ArrayList<>());
        }
        appointment.getStatusHistory().add(Appointment.StatusHistoryEntry.builder()
                .fromStatus(oldStatus)
                .toStatus(AppointmentStatus.CANCELLED)
                .changedBy(appointment.getCustomerId())
                .timestamp(LocalDateTime.now())
                .note(cancelReason)
                .build());

        Appointment updatedAppointment = appointmentRepository.save(appointment);
        return appointmentMapper.toResponse(updatedAppointment);
    }

    @Override
    @Transactional
    public AppointmentResponse confirmAppointment(String id) {
        Appointment appointment = appointmentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Appointment", "id", id));

        if (appointment.getStatus() != AppointmentStatus.PENDING_CONFIRMATION) {
            throw new BadRequestException("Only pending appointments can be confirmed. Current status: " + appointment.getStatus());
        }

        appointment.setStatus(AppointmentStatus.CONFIRMED);

        // Add to status history
        if (appointment.getStatusHistory() == null) {
            appointment.setStatusHistory(new ArrayList<>());
        }
        appointment.getStatusHistory().add(Appointment.StatusHistoryEntry.builder()
                .fromStatus(AppointmentStatus.PENDING_CONFIRMATION)
                .toStatus(AppointmentStatus.CONFIRMED)
                .timestamp(LocalDateTime.now())
                .note("Appointment confirmed by receptionist")
                .build());

        Appointment updatedAppointment = appointmentRepository.save(appointment);
        return appointmentMapper.toResponse(updatedAppointment);
    }

    @Override
    @Transactional
    public AppointmentResponse updateAppointmentStatus(String id, AppointmentStatus newStatus, String note, String userId) {
        Appointment appointment = appointmentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Appointment", "id", id));

        AppointmentStatus currentStatus = appointment.getStatus();

        // Validate state transition
        if (!isValidStatusTransition(currentStatus, newStatus)) {
            throw new BadRequestException("Invalid status transition from " + currentStatus + " to " + newStatus);
        }

        appointment.setStatus(newStatus);

        // Add to status history
        if (appointment.getStatusHistory() == null) {
            appointment.setStatusHistory(new ArrayList<>());
        }
        appointment.getStatusHistory().add(Appointment.StatusHistoryEntry.builder()
                .fromStatus(currentStatus)
                .toStatus(newStatus)
                .changedBy(userId)
                .timestamp(LocalDateTime.now())
                .note(note)
                .build());

        Appointment updatedAppointment = appointmentRepository.save(appointment);
        return appointmentMapper.toResponse(updatedAppointment);
    }

    @Override
    public PageResponse<AppointmentResponse> getAllAppointmentsForGarage(String garageId, AppointmentStatus status, Pageable pageable) {
        Page<Appointment> page;
        if (status != null) {
            page = appointmentRepository.findByGarageIdAndStatus(garageId, status, pageable);
        } else {
            page = appointmentRepository.findByGarageId(garageId, pageable);
        }
        return mapPageToPageResponse(page);
    }

    @Override
    public boolean hasTimeSlotConflict(String garageId, String appointmentDateStr, String timeSlotStart, String timeSlotEnd) {
        LocalDate appointmentDate = LocalDate.parse(appointmentDateStr, DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        LocalDateTime dateTime = appointmentDate.atStartOfDay();

        List<Appointment> conflicts = appointmentRepository.findTimeSlotConflicts(
                garageId,
                dateTime,
                timeSlotStart,
                timeSlotEnd
        );

        return !conflicts.isEmpty();
    }

    // Helper methods

    private String generateAppointmentNumber(String garageId, LocalDateTime appointmentDate) {
        String datePrefix = appointmentDate.format(DATE_FORMATTER);
        String searchPrefix = "APT-" + datePrefix + "-";

        long count = appointmentRepository.countByGarageIdAndAppointmentDate(garageId, appointmentDate);
        int sequenceNumber = (int) (count + 1);

        return String.format("%s%03d", searchPrefix, sequenceNumber);
    }

    private boolean isValidStatusTransition(AppointmentStatus from, AppointmentStatus to) {
        // Any status can transition to CANCELLED
        if (to == AppointmentStatus.CANCELLED) {
            return true;
        }

        return switch (from) {
            case PENDING_CONFIRMATION -> to == AppointmentStatus.CONFIRMED;
            case CONFIRMED -> to == AppointmentStatus.VEHICLE_RECEIVED;
            case VEHICLE_RECEIVED -> to == AppointmentStatus.INSPECTING;
            case INSPECTING -> to == AppointmentStatus.AWAITING_QUOTE_APPROVAL;
            case AWAITING_QUOTE_APPROVAL -> to == AppointmentStatus.REPAIRING;
            case REPAIRING -> to == AppointmentStatus.COMPLETED;
            case COMPLETED -> to == AppointmentStatus.DELIVERED;
            case DELIVERED, CANCELLED -> false;
        };
    }

    private PageResponse<AppointmentResponse> mapPageToPageResponse(Page<Appointment> page) {
        return PageResponse.<AppointmentResponse>builder()
                .content(appointmentMapper.toResponseList(page.getContent()))
                .pageNo(page.getNumber())
                .pageSize(page.getSize())
                .totalElements(page.getTotalElements())
                .totalPages(page.getTotalPages())
                .last(page.isLast())
                .build();
    }
}
