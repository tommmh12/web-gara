package com.webgara.module.appointment.service;

import com.webgara.common.dto.PageResponse;
import com.webgara.module.appointment.dto.AppointmentRequest;
import com.webgara.module.appointment.dto.AppointmentResponse;
import com.webgara.module.appointment.model.AppointmentStatus;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface AppointmentService {

    /**
     * Create a new appointment
     * @param customerId Customer ID
     * @param request Appointment request
     * @return Created appointment response
     */
    AppointmentResponse createAppointment(String customerId, AppointmentRequest request);

    /**
     * Get appointment by ID
     * @param id Appointment ID
     * @return Appointment response
     */
    AppointmentResponse getAppointmentById(String id);

    /**
     * Get all appointments for a customer
     * @param customerId Customer ID
     * @param pageable Pagination
     * @return Page of appointments
     */
    PageResponse<AppointmentResponse> getMyAppointments(String customerId, Pageable pageable);

    /**
     * Cancel an appointment
     * @param id Appointment ID
     * @param cancelReason Reason for cancellation
     * @return Cancelled appointment response
     */
    AppointmentResponse cancelAppointment(String id, String cancelReason);

    /**
     * Confirm an appointment (receptionist action)
     * @param id Appointment ID
     * @return Confirmed appointment response
     */
    AppointmentResponse confirmAppointment(String id);

    /**
     * Update appointment status
     * @param id Appointment ID
     * @param newStatus New status
     * @param note Status change note
     * @param userId User ID performing the action
     * @return Updated appointment response
     */
    AppointmentResponse updateAppointmentStatus(String id, AppointmentStatus newStatus, String note, String userId);

    /**
     * Get all appointments for a garage with pagination and filters
     * @param garageId Garage ID
     * @param status Optional status filter
     * @param pageable Pagination
     * @return Page of appointments
     */
    PageResponse<AppointmentResponse> getAllAppointmentsForGarage(String garageId, AppointmentStatus status, Pageable pageable);

    /**
     * Check if there's a time slot conflict
     * @param garageId Garage ID
     * @param appointmentDateStr Appointment date (format: YYYY-MM-DD)
     * @param timeSlotStart Start time (format: HH:mm)
     * @param timeSlotEnd End time (format: HH:mm)
     * @return true if conflict exists, false otherwise
     */
    boolean hasTimeSlotConflict(String garageId, String appointmentDateStr, String timeSlotStart, String timeSlotEnd);
}
